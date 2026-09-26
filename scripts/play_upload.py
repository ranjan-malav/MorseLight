#!/usr/bin/env python3
"""Publish MorseLight to Google Play via the Play Developer API (no fastlane / Ruby needed).

Setup (once):
    pip3 install --user -r scripts/requirements.txt
    # service-account key with Play access, saved as the gitignored play-service-account.json

Usage:
    python3 scripts/play_upload.py validate
    python3 scripts/play_upload.py status
    python3 scripts/play_upload.py upload                      # AAB -> internal, status completed
    python3 scripts/play_upload.py upload --track internal --draft
    python3 scripts/play_upload.py promote 13 --to production --rollout 0.1   # staged 10%
    python3 scripts/play_upload.py promote 13 --to production                 # full rollout

Add --dry-run to upload/promote to have Play validate the edit and then discard it.

Release notes come from fastlane/metadata/android/<language>/changelogs/<versionCode>.txt (the same
files fastlane uses), falling back to default.txt; every language folder present is sent. The store
listing's default language is en-GB, so keep an en-GB copy. Build the bundle first:
    ./gradlew :app:bundleRelease
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

try:
    from google.oauth2 import service_account
    from googleapiclient.discovery import build
    from googleapiclient.errors import HttpError
    from googleapiclient.http import MediaFileUpload
except ImportError:
    sys.exit("Missing Google API libs. Run: pip3 install --user -r scripts/requirements.txt")

ROOT = Path(__file__).resolve().parent.parent
PACKAGE = "com.ranjan.malav.morselight_flashlightwithmorsecode"
DEFAULT_KEY = ROOT / "play-service-account.json"
DEFAULT_AAB = ROOT / "app/build/outputs/bundle/release/app-release.aab"
METADATA = ROOT / "fastlane/metadata/android"   # <language>/changelogs/<versionCode>.txt
SCOPES = ["https://www.googleapis.com/auth/androidpublisher"]


def service(key: Path):
    if not key.exists():
        sys.exit(f"Service-account key not found: {key}")
    creds = service_account.Credentials.from_service_account_file(str(key), scopes=SCOPES)
    return build("androidpublisher", "v3", credentials=creds, cache_discovery=False)


def version_name() -> str:
    gradle = (ROOT / "app/build.gradle.kts").read_text()
    m = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
    return m.group(1) if m else "?"


def release_notes(version_code: int):
    """One entry per language folder that has <versionCode>.txt (or default.txt)."""
    notes = []
    for lang_dir in sorted(p for p in METADATA.iterdir() if (p / "changelogs").is_dir()):
        for name in (f"{version_code}.txt", "default.txt"):
            f = lang_dir / "changelogs" / name
            if f.exists():
                text = f.read_text().strip()
                if len(text) > 500:
                    sys.exit(f"{f} is {len(text)} chars; Play's limit is 500.")
                notes.append({"language": lang_dir.name, "text": text})
                break
    if notes:
        print("  release notes:", ", ".join(n["language"] for n in notes))
    else:
        print("  (no release notes file found; publishing without notes)")
    return notes


def release_body(version_code: int, draft: bool, rollout: float | None):
    body = {
        "name": f"{version_code} ({version_name()})",
        "versionCodes": [str(version_code)],
        "releaseNotes": release_notes(version_code),
    }
    if draft:
        body["status"] = "draft"
    elif rollout is not None and rollout < 1.0:
        body["status"] = "inProgress"
        body["userFraction"] = rollout
    else:
        body["status"] = "completed"
    return body


class Edit:
    """Context manager: opens an edit, commits on success (or validates + discards on dry run),
    and always deletes it on failure so nothing half-done is left behind."""

    def __init__(self, svc, dry_run=False):
        self.svc, self.dry_run, self.id = svc, dry_run, None

    def __enter__(self):
        self.id = self.svc.edits().insert(packageName=PACKAGE, body={}).execute()["id"]
        return self

    def __exit__(self, exc_type, exc, tb):
        edits = self.svc.edits()
        if exc_type is None and not self.dry_run:
            edits.commit(packageName=PACKAGE, editId=self.id).execute()
            print("Committed.")
            return False
        if exc_type is None and self.dry_run:
            edits.validate(packageName=PACKAGE, editId=self.id).execute()
            print("Dry run: Play validated the edit. Discarding it (nothing published).")
        try:
            edits.delete(packageName=PACKAGE, editId=self.id).execute()
        except HttpError:
            pass
        return False


def cmd_validate(svc, _args):
    with Edit(svc, dry_run=True):
        print("Service account is valid and has access to", PACKAGE)


def cmd_status(svc, _args):
    edit_id = svc.edits().insert(packageName=PACKAGE, body={}).execute()["id"]
    try:
        tracks = svc.edits().tracks().list(packageName=PACKAGE, editId=edit_id).execute()
        for t in tracks.get("tracks", []):
            releases = t.get("releases", [])
            if not releases:
                continue
            print(f"{t['track']}:")
            for r in releases:
                frac = f" ({r['userFraction']:.0%})" if "userFraction" in r else ""
                print(f"  {r.get('name', '?')}  status={r.get('status')}{frac}  "
                      f"versionCodes={r.get('versionCodes')}")
    finally:
        svc.edits().delete(packageName=PACKAGE, editId=edit_id).execute()


def cmd_upload(svc, args):
    aab = Path(args.aab)
    if not aab.exists():
        sys.exit(f"Bundle not found: {aab}\nBuild it first: ./gradlew :app:bundleRelease")
    with Edit(svc, dry_run=args.dry_run) as e:
        print(f"Uploading {aab.name} ...")
        media = MediaFileUpload(str(aab), mimetype="application/octet-stream", resumable=True)
        vc = svc.edits().bundles().upload(
            packageName=PACKAGE, editId=e.id, media_body=media).execute()["versionCode"]
        body = release_body(vc, args.draft, args.rollout)
        svc.edits().tracks().update(packageName=PACKAGE, editId=e.id, track=args.track,
                                    body={"releases": [body]}).execute()
        print(f"versionCode {vc} -> {args.track} ({body['status']})")


def cmd_promote(svc, args):
    with Edit(svc, dry_run=args.dry_run) as e:
        body = release_body(args.version_code, False, args.rollout)
        svc.edits().tracks().update(packageName=PACKAGE, editId=e.id, track=args.to,
                                    body={"releases": [body]}).execute()
        frac = f" at {args.rollout:.0%}" if body["status"] == "inProgress" else ""
        print(f"versionCode {args.version_code} -> {args.to} ({body['status']}{frac})")


def fraction(value: str) -> float:
    f = float(value)
    if not 0 < f <= 1:
        raise argparse.ArgumentTypeError("rollout must be in (0, 1], e.g. 0.1 for 10%")
    return f


def main():
    p = argparse.ArgumentParser(description="Publish MorseLight to Google Play.")
    p.add_argument("--key", default=str(DEFAULT_KEY), help="service-account JSON key")
    sub = p.add_subparsers(dest="cmd", required=True)

    sub.add_parser("validate", help="check the key + app access (changes nothing)")
    sub.add_parser("status", help="show releases on every track")

    up = sub.add_parser("upload", help="upload the App Bundle to a track")
    up.add_argument("--aab", default=str(DEFAULT_AAB))
    up.add_argument("--track", default="internal")
    up.add_argument("--draft", action="store_true", help="create as a draft release")
    up.add_argument("--rollout", type=fraction, help="staged rollout fraction (0-1]")
    up.add_argument("--dry-run", action="store_true")

    pr = sub.add_parser("promote", help="release an already-uploaded versionCode to a track")
    pr.add_argument("version_code", type=int)
    pr.add_argument("--to", default="production")
    pr.add_argument("--rollout", type=fraction, help="staged rollout fraction (0-1]")
    pr.add_argument("--dry-run", action="store_true")

    args = p.parse_args()
    svc = service(Path(args.key))
    try:
        {"validate": cmd_validate, "status": cmd_status,
         "upload": cmd_upload, "promote": cmd_promote}[args.cmd](svc, args)
    except HttpError as err:
        sys.exit(f"Play API error {err.status_code}: {err.reason}")


if __name__ == "__main__":
    main()
