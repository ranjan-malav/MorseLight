/* @ds-bundle: {"format":4,"namespace":"PersonalUIDesignSystem_06a1d8","components":[{"name":"Badge","sourcePath":"components/core/Badge.jsx"},{"name":"Button","sourcePath":"components/core/Button.jsx"},{"name":"Card","sourcePath":"components/core/Card.jsx"},{"name":"CardHeader","sourcePath":"components/core/Card.jsx"},{"name":"Icon","sourcePath":"components/core/Icon.jsx"},{"name":"IconButton","sourcePath":"components/core/IconButton.jsx"},{"name":"Tag","sourcePath":"components/core/Tag.jsx"},{"name":"ProgressRing","sourcePath":"components/data/ProgressRing.jsx"},{"name":"Stat","sourcePath":"components/data/Stat.jsx"},{"name":"Dialog","sourcePath":"components/feedback/Dialog.jsx"},{"name":"Toast","sourcePath":"components/feedback/Toast.jsx"},{"name":"Tooltip","sourcePath":"components/feedback/Tooltip.jsx"},{"name":"Checkbox","sourcePath":"components/forms/Checkbox.jsx"},{"name":"Field","sourcePath":"components/forms/Field.jsx"},{"name":"Input","sourcePath":"components/forms/Input.jsx"},{"name":"Radio","sourcePath":"components/forms/Radio.jsx"},{"name":"Select","sourcePath":"components/forms/Select.jsx"},{"name":"Slider","sourcePath":"components/forms/Slider.jsx"},{"name":"Switch","sourcePath":"components/forms/Switch.jsx"},{"name":"ListRow","sourcePath":"components/navigation/ListRow.jsx"},{"name":"SegmentedControl","sourcePath":"components/navigation/SegmentedControl.jsx"},{"name":"Tabs","sourcePath":"components/navigation/Tabs.jsx"}],"sourceHashes":{"components/core/Badge.jsx":"d92cbda7644c","components/core/Button.jsx":"d33f821f5ad8","components/core/Card.jsx":"473ee8e515e0","components/core/Icon.jsx":"2716e16769d0","components/core/IconButton.jsx":"6d14278c1992","components/core/Tag.jsx":"746603d5ba11","components/data/ProgressRing.jsx":"29ba1e577e30","components/data/Stat.jsx":"47002e49751c","components/feedback/Dialog.jsx":"d44e40f3ffad","components/feedback/Toast.jsx":"11f72064e9a6","components/feedback/Tooltip.jsx":"1ef97d3fd370","components/forms/Checkbox.jsx":"457d9362b3a8","components/forms/Field.jsx":"37d8b1ea859f","components/forms/Input.jsx":"fcbd63453ec3","components/forms/Radio.jsx":"dc49de0d2650","components/forms/Select.jsx":"18e4a09a40c6","components/forms/Slider.jsx":"17b9d3964031","components/forms/Switch.jsx":"410cb9a94372","components/navigation/ListRow.jsx":"71fc558e382d","components/navigation/SegmentedControl.jsx":"402716ddf3b9","components/navigation/Tabs.jsx":"014dfba96d67","ui_kits/dashboard/Chrome.jsx":"a124dadbc958","ui_kits/dashboard/HealthView.jsx":"17f9ba94d96b","ui_kits/dashboard/TodayView.jsx":"1760303fa392","ui_kits/habits_app/Phone.jsx":"052d162e817e","ui_kits/habits_app/Screens.jsx":"d1007e5b6afe","ui_kits/internal_tool/RunsTable.jsx":"e43d1860a989","ui_kits/internal_tool/Shell.jsx":"95ed91be1ab4"},"inlinedExternals":[],"unexposedExports":[]} */

(() => {

const __ds_ns = (window.PersonalUIDesignSystem_06a1d8 = window.PersonalUIDesignSystem_06a1d8 || {});

const __ds_scope = {};

(__ds_ns.__errors = __ds_ns.__errors || []);

// components/core/Card.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
const TINTS = {
  none: 'var(--surface-card)',
  accent: 'var(--accent-soft)',
  success: 'var(--success-soft)',
  warning: 'var(--warning-soft)',
  info: 'var(--info-soft)',
  sunken: 'var(--surface-sunken)'
};
function Card({
  children,
  tint = 'none',
  elevation = 1,
  padding = 'md',
  interactive = false,
  as: Tag = 'div',
  style,
  ...rest
}) {
  const pad = padding === 'none' ? 0 : padding === 'sm' ? 'var(--space-5)' : padding === 'lg' ? 'var(--pad-card-lg)' : 'var(--pad-card)';
  const shadow = ['var(--shadow-0)', 'var(--shadow-1)', 'var(--shadow-2)', 'var(--shadow-3)'][elevation] || 'var(--shadow-1)';
  return /*#__PURE__*/React.createElement(Tag, _extends({}, rest, {
    style: {
      background: TINTS[tint] || TINTS.none,
      border: '1px solid ' + (tint === 'none' || tint === 'sunken' ? 'var(--border-subtle)' : 'transparent'),
      borderRadius: 'var(--radius-card)',
      padding: pad,
      boxShadow: tint === 'none' ? shadow : 'none',
      transition: 'box-shadow var(--dur-base) var(--ease-standard), transform var(--dur-base) var(--ease-emphasized)',
      cursor: interactive ? 'pointer' : undefined,
      ...style
    },
    onPointerEnter: interactive ? ev => {
      ev.currentTarget.style.boxShadow = 'var(--shadow-3)';
      ev.currentTarget.style.transform = 'translateY(var(--lift-y))';
    } : undefined,
    onPointerLeave: interactive ? ev => {
      ev.currentTarget.style.boxShadow = tint === 'none' ? shadow : 'none';
      ev.currentTarget.style.transform = 'none';
    } : undefined
  }), children);
}
function CardHeader({
  title,
  subtitle,
  action,
  style,
  ...rest
}) {
  return /*#__PURE__*/React.createElement("div", _extends({}, rest, {
    style: {
      display: 'flex',
      alignItems: 'flex-start',
      justifyContent: 'space-between',
      gap: 'var(--space-5)',
      marginBottom: 'var(--gap-stack)',
      ...style
    }
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 2,
      minWidth: 0
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-title-3)',
      color: 'var(--text-heading)',
      letterSpacing: 'var(--tracking-title)'
    }
  }, title), subtitle ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-muted)'
    }
  }, subtitle) : null), action);
}
Object.assign(__ds_scope, { Card, CardHeader });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Card.jsx", error: String((e && e.message) || e) }); }

// components/core/Icon.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
const LUCIDE_BASE = 'https://unpkg.com/lucide-static@0.544.0/icons/';

/* Renders a Lucide glyph as a CSS mask so it inherits currentColor.
   No inline path data: the SVG is fetched from the icon CDN. */
function Icon({
  name,
  size = 20,
  strokeWidth,
  color = 'currentColor',
  label,
  style,
  ...rest
}) {
  const url = `url("${LUCIDE_BASE}${name}.svg")`;
  return /*#__PURE__*/React.createElement("span", _extends({
    role: label ? 'img' : undefined,
    "aria-label": label,
    "aria-hidden": label ? undefined : true
  }, rest, {
    style: {
      display: 'inline-block',
      flex: '0 0 auto',
      width: size,
      height: size,
      background: color,
      WebkitMaskImage: url,
      maskImage: url,
      WebkitMaskRepeat: 'no-repeat',
      maskRepeat: 'no-repeat',
      WebkitMaskPosition: 'center',
      maskPosition: 'center',
      WebkitMaskSize: 'contain',
      maskSize: 'contain',
      ...style
    }
  }));
}
Object.assign(__ds_scope, { Icon });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Icon.jsx", error: String((e && e.message) || e) }); }

// components/core/Badge.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
const TONES = {
  neutral: ['var(--surface-sunken)', 'var(--text-muted)'],
  accent: ['var(--accent-soft)', 'var(--accent-on-soft)'],
  success: ['var(--success-soft)', 'var(--success-on-soft)'],
  warning: ['var(--warning-soft)', 'var(--warning-on-soft)'],
  danger: ['var(--danger-soft)', 'var(--danger-on-soft)'],
  info: ['var(--info-soft)', 'var(--info-on-soft)']
};
function Badge({
  children,
  tone = 'neutral',
  icon,
  dot = false,
  size = 'md',
  style,
  ...rest
}) {
  const [bg, fg] = TONES[tone] || TONES.neutral;
  const sm = size === 'sm';
  return /*#__PURE__*/React.createElement("span", _extends({}, rest, {
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: sm ? 4 : 6,
      height: sm ? 20 : 24,
      padding: sm ? '0 8px' : '0 10px',
      borderRadius: 'var(--radius-full)',
      background: bg,
      color: fg,
      font: `var(--weight-semibold) ${sm ? 'var(--text-caption-2)' : 'var(--text-caption-1)'}/1 var(--font-core)`,
      whiteSpace: 'nowrap',
      ...style
    }
  }), dot ? /*#__PURE__*/React.createElement("span", {
    style: {
      width: 6,
      height: 6,
      borderRadius: 999,
      background: 'currentColor'
    }
  }) : null, icon ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: icon,
    size: sm ? 12 : 14
  }) : null, children);
}
Object.assign(__ds_scope, { Badge });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Badge.jsx", error: String((e && e.message) || e) }); }

// components/core/Button.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
const SIZES = {
  sm: {
    h: 'var(--control-h-sm)',
    px: 14,
    font: 'var(--text-footnote)',
    icon: 16,
    gap: 6
  },
  md: {
    h: 'var(--control-h-md)',
    px: 18,
    font: 'var(--text-subhead)',
    icon: 18,
    gap: 8
  },
  lg: {
    h: 'var(--control-h-lg)',
    px: 24,
    font: 'var(--text-body-size)',
    icon: 20,
    gap: 10
  }
};
function skin(variant, tone) {
  const accent = tone === 'danger' ? 'var(--danger)' : tone === 'success' ? 'var(--success)' : 'var(--accent)';
  const soft = tone === 'danger' ? 'var(--danger-soft)' : tone === 'success' ? 'var(--success-soft)' : 'var(--accent-soft)';
  const onSoft = tone === 'danger' ? 'var(--danger-on-soft)' : tone === 'success' ? 'var(--success-on-soft)' : 'var(--accent-on-soft)';
  switch (variant) {
    case 'secondary':
      return {
        background: 'var(--surface-card)',
        color: 'var(--text-body)',
        border: '1px solid var(--border-default)',
        boxShadow: 'var(--shadow-1)'
      };
    case 'soft':
      return {
        background: soft,
        color: onSoft,
        border: '1px solid transparent',
        boxShadow: 'none'
      };
    case 'ghost':
      return {
        background: 'transparent',
        color: accent,
        border: '1px solid transparent',
        boxShadow: 'none'
      };
    case 'destructive':
      return {
        background: 'var(--danger)',
        color: '#fff',
        border: '1px solid transparent',
        boxShadow: 'var(--shadow-2)'
      };
    default:
      return {
        background: accent,
        color: 'var(--text-on-accent)',
        border: '1px solid transparent',
        boxShadow: 'var(--shadow-accent)'
      };
  }
}
function Button({
  children,
  variant = 'primary',
  size = 'md',
  tone = 'accent',
  icon,
  iconAfter,
  block = false,
  loading = false,
  disabled = false,
  type = 'button',
  style,
  ...rest
}) {
  const s = SIZES[size] || SIZES.md;
  const isFlat = variant === 'ghost' || variant === 'soft';
  const off = disabled || loading;
  return /*#__PURE__*/React.createElement("button", _extends({
    type: type,
    disabled: off,
    "aria-busy": loading || undefined
  }, rest, {
    style: {
      display: block ? 'flex' : 'inline-flex',
      width: block ? '100%' : undefined,
      alignItems: 'center',
      justifyContent: 'center',
      gap: s.gap,
      height: s.h,
      padding: `0 ${s.px}px`,
      borderRadius: 'var(--radius-control)',
      font: `var(--weight-semibold) ${s.font}/1 var(--font-core)`,
      letterSpacing: '-0.005em',
      cursor: off ? 'not-allowed' : 'pointer',
      opacity: off ? 0.45 : 1,
      transition: 'transform var(--dur-instant) var(--ease-standard), background var(--dur-fast) var(--ease-standard), box-shadow var(--dur-fast) var(--ease-standard), filter var(--dur-fast) var(--ease-standard)',
      WebkitTapHighlightColor: 'transparent',
      ...skin(variant, tone),
      ...style
    },
    onPointerDown: ev => {
      if (!off) ev.currentTarget.style.transform = 'scale(var(--press-scale))';
    },
    onPointerUp: ev => {
      ev.currentTarget.style.transform = 'none';
    },
    onPointerLeave: ev => {
      ev.currentTarget.style.transform = 'none';
      ev.currentTarget.style.filter = 'none';
      if (isFlat) ev.currentTarget.style.background = variant === 'ghost' ? 'transparent' : skin(variant, tone).background;
    },
    onPointerEnter: ev => {
      if (off) return;
      if (variant === 'ghost') ev.currentTarget.style.background = 'var(--surface-hover)';else if (variant === 'soft') ev.currentTarget.style.filter = 'brightness(.97)';else ev.currentTarget.style.filter = 'brightness(.94)';
    }
  }), loading ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: "loader-circle",
    size: s.icon,
    style: {
      animation: 'pui-spin 900ms linear infinite'
    }
  }) : icon ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: icon,
    size: s.icon
  }) : null, children, iconAfter ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: iconAfter,
    size: s.icon
  }) : null);
}
Object.assign(__ds_scope, { Button });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Button.jsx", error: String((e && e.message) || e) }); }

// components/core/IconButton.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
const DIM = {
  sm: 32,
  md: 40,
  lg: 48
};
const GLYPH = {
  sm: 16,
  md: 20,
  lg: 22
};
function IconButton({
  icon,
  label,
  variant = 'ghost',
  size = 'md',
  tone = 'accent',
  active = false,
  disabled = false,
  style,
  ...rest
}) {
  const d = DIM[size] || DIM.md;
  const filled = variant === 'filled';
  const soft = variant === 'soft' || active;
  return /*#__PURE__*/React.createElement("button", _extends({
    type: "button",
    "aria-label": label,
    "aria-pressed": active || undefined,
    disabled: disabled
  }, rest, {
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: d,
      height: d,
      borderRadius: 'var(--radius-full)',
      border: variant === 'outline' ? '1px solid var(--border-default)' : '1px solid transparent',
      background: filled ? tone === 'danger' ? 'var(--danger)' : 'var(--accent)' : soft ? 'var(--accent-soft)' : variant === 'outline' ? 'var(--surface-card)' : 'transparent',
      color: filled ? 'var(--text-on-accent)' : soft ? 'var(--accent-on-soft)' : tone === 'danger' ? 'var(--danger)' : 'var(--text-muted)',
      cursor: disabled ? 'not-allowed' : 'pointer',
      opacity: disabled ? 0.4 : 1,
      transition: 'background var(--dur-fast) var(--ease-standard), transform var(--dur-instant) var(--ease-standard), color var(--dur-fast) var(--ease-standard)',
      ...style
    },
    onPointerEnter: ev => {
      if (!disabled && !filled) ev.currentTarget.style.background = soft ? 'var(--surface-press)' : 'var(--surface-hover)';
    },
    onPointerLeave: ev => {
      ev.currentTarget.style.transform = 'none';
      if (!filled) ev.currentTarget.style.background = soft ? 'var(--accent-soft)' : variant === 'outline' ? 'var(--surface-card)' : 'transparent';
    },
    onPointerDown: ev => {
      if (!disabled) ev.currentTarget.style.transform = 'scale(var(--press-scale))';
    },
    onPointerUp: ev => {
      ev.currentTarget.style.transform = 'none';
    }
  }), /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: icon,
    size: GLYPH[size] || 20
  }));
}
Object.assign(__ds_scope, { IconButton });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/IconButton.jsx", error: String((e && e.message) || e) }); }

// components/core/Tag.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Tag({
  children,
  selected = false,
  onRemove,
  icon,
  color,
  onClick,
  style,
  ...rest
}) {
  const clickable = !!onClick;
  return /*#__PURE__*/React.createElement("span", _extends({}, rest, {
    onClick: onClick,
    role: clickable ? 'button' : undefined,
    tabIndex: clickable ? 0 : undefined,
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: 6,
      height: 30,
      padding: onRemove ? '0 6px 0 12px' : '0 12px',
      borderRadius: 'var(--radius-chip)',
      background: selected ? 'var(--accent)' : 'var(--surface-card)',
      color: selected ? 'var(--text-on-accent)' : 'var(--text-body)',
      border: '1px solid ' + (selected ? 'transparent' : 'var(--border-default)'),
      font: 'var(--weight-medium) var(--text-footnote)/1 var(--font-core)',
      cursor: clickable ? 'pointer' : 'default',
      transition: 'background var(--dur-fast) var(--ease-standard), border-color var(--dur-fast) var(--ease-standard)',
      ...style
    },
    onPointerEnter: ev => {
      if (clickable && !selected) ev.currentTarget.style.background = 'var(--surface-hover)';
    },
    onPointerLeave: ev => {
      if (clickable && !selected) ev.currentTarget.style.background = 'var(--surface-card)';
    }
  }), color ? /*#__PURE__*/React.createElement("span", {
    style: {
      width: 8,
      height: 8,
      borderRadius: 999,
      background: color
    }
  }) : null, icon ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: icon,
    size: 14
  }) : null, children, onRemove ? /*#__PURE__*/React.createElement("span", {
    role: "button",
    "aria-label": "Remove",
    onClick: ev => {
      ev.stopPropagation();
      onRemove(ev);
    },
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: 20,
      height: 20,
      borderRadius: 999,
      background: selected ? 'rgba(255,255,255,.2)' : 'var(--surface-sunken)',
      cursor: 'pointer'
    }
  }, /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: "x",
    size: 12
  })) : null);
}
Object.assign(__ds_scope, { Tag });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/core/Tag.jsx", error: String((e && e.message) || e) }); }

// components/data/ProgressRing.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function ProgressRing({
  value = 0,
  size = 64,
  thickness,
  tone = 'accent',
  label,
  track = 'var(--track)',
  style,
  ...rest
}) {
  const t = thickness || Math.max(4, Math.round(size * 0.11));
  const pct = Math.max(0, Math.min(100, value));
  const color = tone === 'success' ? 'var(--success)' : tone === 'warning' ? 'var(--warning)' : tone === 'danger' ? 'var(--danger)' : tone === 'info' ? 'var(--info)' : 'var(--accent)';
  return /*#__PURE__*/React.createElement("div", _extends({
    role: "img",
    "aria-label": typeof label === 'string' ? label : `${pct}%`
  }, rest, {
    style: {
      position: 'relative',
      flex: '0 0 auto',
      width: size,
      height: size,
      borderRadius: '50%',
      background: `conic-gradient(${color} ${pct * 3.6}deg, ${track} 0deg)`,
      display: 'grid',
      placeItems: 'center',
      transition: 'background var(--dur-slow) var(--ease-standard)',
      ...style
    }
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      width: size - t * 2,
      height: size - t * 2,
      borderRadius: '50%',
      background: 'var(--surface-card)',
      display: 'grid',
      placeItems: 'center'
    }
  }, label != null ? /*#__PURE__*/React.createElement("span", {
    className: "tabular",
    style: {
      font: `var(--weight-bold) ${Math.max(10, Math.round(size * 0.24))}px/1 var(--font-core)`,
      color: 'var(--text-heading)'
    }
  }, label) : null));
}
Object.assign(__ds_scope, { ProgressRing });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/data/ProgressRing.jsx", error: String((e && e.message) || e) }); }

// components/data/Stat.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Stat({
  label,
  value,
  unit,
  delta,
  deltaTone,
  caption,
  icon,
  align = 'left',
  style,
  ...rest
}) {
  const tone = deltaTone || (typeof delta === 'string' && delta.trim().startsWith('-') ? 'danger' : 'success');
  const color = tone === 'danger' ? 'var(--danger)' : tone === 'neutral' ? 'var(--text-muted)' : 'var(--success)';
  return /*#__PURE__*/React.createElement("div", _extends({}, rest, {
    style: {
      display: 'grid',
      gap: 6,
      justifyItems: align === 'center' ? 'center' : 'start',
      ...style
    }
  }), /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: 6,
      font: 'var(--type-caption)',
      letterSpacing: 'var(--tracking-caps)',
      textTransform: 'uppercase',
      color: 'var(--text-subtle)'
    }
  }, icon ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: icon,
    size: 14
  }) : null, label), /*#__PURE__*/React.createElement("span", {
    className: "tabular",
    style: {
      display: 'inline-flex',
      alignItems: 'baseline',
      gap: 4,
      font: 'var(--type-metric)',
      color: 'var(--text-heading)',
      letterSpacing: 'var(--tracking-display)'
    }
  }, value, unit ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-subhead)',
      fontWeight: 'var(--weight-semibold)',
      color: 'var(--text-muted)'
    }
  }, unit) : null), delta != null || caption ? /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: 6,
      font: 'var(--type-footnote)',
      color: 'var(--text-muted)'
    }
  }, delta != null ? /*#__PURE__*/React.createElement("span", {
    className: "tabular",
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: 2,
      color,
      fontWeight: 'var(--weight-semibold)'
    }
  }, /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: tone === 'danger' ? 'trending-down' : 'trending-up',
    size: 14
  }), delta) : null, caption) : null);
}
Object.assign(__ds_scope, { Stat });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/data/Stat.jsx", error: String((e && e.message) || e) }); }

// components/feedback/Dialog.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Dialog({
  open = true,
  title,
  description,
  children,
  actions,
  onClose,
  width = 420,
  icon,
  tone = 'accent',
  style,
  ...rest
}) {
  if (!open) return null;
  const soft = tone === 'danger' ? 'var(--danger-soft)' : tone === 'success' ? 'var(--success-soft)' : 'var(--accent-soft)';
  const fg = tone === 'danger' ? 'var(--danger)' : tone === 'success' ? 'var(--success)' : 'var(--accent)';
  return /*#__PURE__*/React.createElement("div", {
    onClick: onClose,
    style: {
      position: 'absolute',
      inset: 0,
      zIndex: 40,
      display: 'grid',
      placeItems: 'center',
      padding: 'var(--space-6)',
      background: 'var(--surface-overlay)',
      backdropFilter: 'blur(3px)',
      WebkitBackdropFilter: 'blur(3px)',
      animation: 'pui-fade var(--dur-base) var(--ease-standard)'
    }
  }, /*#__PURE__*/React.createElement("div", _extends({
    role: "dialog",
    "aria-modal": "true",
    onClick: ev => ev.stopPropagation()
  }, rest, {
    style: {
      width: '100%',
      maxWidth: width,
      background: 'var(--surface-card)',
      borderRadius: 'var(--radius-sheet)',
      boxShadow: 'var(--shadow-4)',
      padding: 'var(--pad-card-lg)',
      display: 'grid',
      gap: 'var(--gap-stack)',
      animation: 'pui-rise var(--dur-slow) var(--ease-emphasized)',
      ...style
    }
  }), icon ? /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'grid',
      placeItems: 'center',
      width: 44,
      height: 44,
      borderRadius: 'var(--radius-full)',
      background: soft,
      color: fg
    }
  }, /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: icon,
    size: 22
  })) : null, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 6
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-title-2)',
      color: 'var(--text-heading)',
      letterSpacing: 'var(--tracking-title)'
    }
  }, title), description ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-subhead)',
      color: 'var(--text-muted)',
      textWrap: 'pretty'
    }
  }, description) : null), children, actions ? /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      justifyContent: 'flex-end',
      gap: 'var(--gap-inline)',
      marginTop: 'var(--space-2)'
    }
  }, actions) : null));
}
Object.assign(__ds_scope, { Dialog });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/feedback/Dialog.jsx", error: String((e && e.message) || e) }); }

// components/feedback/Toast.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
const TONES = {
  neutral: ['var(--surface-inverse)', 'var(--text-on-inverse)', 'info'],
  success: ['var(--surface-inverse)', 'var(--text-on-inverse)', 'check-check'],
  danger: ['var(--danger)', '#fff', 'triangle-alert']
};
function Toast({
  message,
  tone = 'neutral',
  action,
  onDismiss,
  icon,
  style,
  ...rest
}) {
  const [bg, fg, fallback] = TONES[tone] || TONES.neutral;
  return /*#__PURE__*/React.createElement("div", _extends({
    role: "status"
  }, rest, {
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      gap: 'var(--space-5)',
      minHeight: 48,
      padding: '0 8px 0 18px',
      background: bg,
      color: fg,
      borderRadius: 'var(--radius-full)',
      boxShadow: 'var(--shadow-4)',
      font: 'var(--type-subhead)',
      animation: 'pui-rise var(--dur-slow) var(--ease-emphasized)',
      ...style
    }
  }), /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: icon || fallback,
    size: 18,
    color: tone === 'success' ? 'var(--success)' : 'currentColor'
  }), /*#__PURE__*/React.createElement("span", {
    style: {
      paddingRight: action || onDismiss ? 0 : 10
    }
  }, message), action ? /*#__PURE__*/React.createElement("button", {
    onClick: action.onClick,
    style: {
      border: 'none',
      background: 'transparent',
      color: 'inherit',
      font: 'var(--weight-semibold) var(--text-subhead)/1 var(--font-core)',
      cursor: 'pointer',
      padding: '0 6px',
      textDecoration: 'underline',
      textUnderlineOffset: 2
    }
  }, action.label) : null, onDismiss ? /*#__PURE__*/React.createElement(__ds_scope.IconButton, {
    icon: "x",
    label: "Dismiss",
    size: "sm",
    onClick: onDismiss,
    style: {
      color: 'inherit',
      opacity: .8
    }
  }) : null);
}
Object.assign(__ds_scope, { Toast });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/feedback/Toast.jsx", error: String((e && e.message) || e) }); }

// components/feedback/Tooltip.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Tooltip({
  label,
  children,
  placement = 'top',
  style,
  ...rest
}) {
  const [open, setOpen] = React.useState(false);
  const pos = {
    top: {
      bottom: '100%',
      left: '50%',
      transform: 'translate(-50%,-8px)'
    },
    bottom: {
      top: '100%',
      left: '50%',
      transform: 'translate(-50%,8px)'
    },
    left: {
      right: '100%',
      top: '50%',
      transform: 'translate(-8px,-50%)'
    },
    right: {
      left: '100%',
      top: '50%',
      transform: 'translate(8px,-50%)'
    }
  }[placement];
  return /*#__PURE__*/React.createElement("span", _extends({}, rest, {
    onPointerEnter: () => setOpen(true),
    onPointerLeave: () => setOpen(false),
    onFocus: () => setOpen(true),
    onBlur: () => setOpen(false),
    style: {
      position: 'relative',
      display: 'inline-flex',
      ...style
    }
  }), children, /*#__PURE__*/React.createElement("span", {
    role: "tooltip",
    style: {
      position: 'absolute',
      zIndex: 30,
      ...pos,
      padding: '6px 10px',
      borderRadius: 'var(--radius-sm)',
      background: 'var(--surface-inverse)',
      color: 'var(--text-on-inverse)',
      font: 'var(--type-caption)',
      whiteSpace: 'nowrap',
      pointerEvents: 'none',
      opacity: open ? 1 : 0,
      boxShadow: 'var(--shadow-3)',
      transition: 'opacity var(--dur-fast) var(--ease-standard)'
    }
  }, label));
}
Object.assign(__ds_scope, { Tooltip });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/feedback/Tooltip.jsx", error: String((e && e.message) || e) }); }

// components/forms/Checkbox.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Checkbox({
  checked = false,
  indeterminate = false,
  label,
  description,
  disabled = false,
  onChange,
  style,
  ...rest
}) {
  const on = checked || indeterminate;
  return /*#__PURE__*/React.createElement("label", _extends({}, rest, {
    style: {
      display: 'inline-flex',
      alignItems: description ? 'flex-start' : 'center',
      gap: 10,
      cursor: disabled ? 'not-allowed' : 'pointer',
      opacity: disabled ? 0.45 : 1,
      minHeight: 'var(--space-8)',
      ...style
    }
  }), /*#__PURE__*/React.createElement("input", {
    type: "checkbox",
    checked: checked,
    disabled: disabled,
    onChange: onChange,
    style: {
      position: 'absolute',
      opacity: 0,
      width: 0,
      height: 0
    }
  }), /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: 22,
      height: 22,
      flex: '0 0 auto',
      marginTop: description ? 1 : 0,
      borderRadius: 'var(--radius-xs)',
      background: on ? 'var(--accent)' : 'var(--surface-card)',
      border: '1px solid ' + (on ? 'transparent' : 'var(--border-strong)'),
      color: '#fff',
      boxShadow: on ? 'none' : 'var(--inset-field)',
      transition: 'background var(--dur-fast) var(--ease-standard), border-color var(--dur-fast) var(--ease-standard)'
    }
  }, indeterminate ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: "minus",
    size: 14
  }) : checked ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: "check",
    size: 14
  }) : null), label ? /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'grid',
      gap: 2
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-subhead)',
      color: 'var(--text-body)'
    }
  }, label), description ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-subtle)'
    }
  }, description) : null) : null);
}
Object.assign(__ds_scope, { Checkbox });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/forms/Checkbox.jsx", error: String((e && e.message) || e) }); }

// components/forms/Field.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Field({
  label,
  hint,
  error,
  required = false,
  htmlFor,
  children,
  style,
  ...rest
}) {
  return /*#__PURE__*/React.createElement("div", _extends({}, rest, {
    style: {
      display: 'grid',
      gap: 6,
      ...style
    }
  }), label ? /*#__PURE__*/React.createElement("label", {
    htmlFor: htmlFor,
    style: {
      font: 'var(--type-label)',
      color: 'var(--text-body)',
      display: 'inline-flex',
      gap: 4
    }
  }, label, required ? /*#__PURE__*/React.createElement("span", {
    style: {
      color: 'var(--danger)'
    }
  }, "*") : null) : null, children, error ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--danger)'
    }
  }, error) : hint ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-subtle)'
    }
  }, hint) : null);
}
Object.assign(__ds_scope, { Field });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/forms/Field.jsx", error: String((e && e.message) || e) }); }

// components/forms/Input.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Input({
  icon,
  suffix,
  invalid = false,
  size = 'md',
  multiline = false,
  rows = 3,
  style,
  ...rest
}) {
  const [focus, setFocus] = React.useState(false);
  const h = size === 'sm' ? 'var(--control-h-sm)' : size === 'lg' ? 'var(--control-h-lg)' : 'var(--control-h-md)';
  const Tag = multiline ? 'textarea' : 'input';
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: multiline ? 'flex-start' : 'center',
      gap: 8,
      minHeight: multiline ? undefined : h,
      padding: multiline ? '10px 14px' : '0 14px',
      background: 'var(--surface-card)',
      border: '1px solid ' + (invalid ? 'var(--danger)' : focus ? 'var(--accent)' : 'var(--border-default)'),
      boxShadow: focus ? '0 0 0 3px var(--focus-ring)' : 'var(--inset-field)',
      borderRadius: 'var(--radius-field)',
      transition: 'border-color var(--dur-fast) var(--ease-standard), box-shadow var(--dur-fast) var(--ease-standard)',
      ...style
    }
  }, icon ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: icon,
    size: 18,
    color: "var(--text-subtle)",
    style: {
      marginTop: multiline ? 3 : 0
    }
  }) : null, /*#__PURE__*/React.createElement(Tag, _extends({
    rows: multiline ? rows : undefined
  }, rest, {
    onFocus: ev => {
      setFocus(true);
      rest.onFocus && rest.onFocus(ev);
    },
    onBlur: ev => {
      setFocus(false);
      rest.onBlur && rest.onBlur(ev);
    },
    style: {
      flex: 1,
      minWidth: 0,
      border: 'none',
      outline: 'none',
      background: 'transparent',
      font: size === 'sm' ? 'var(--type-footnote)' : 'var(--type-subhead)',
      color: 'var(--text-body)',
      resize: multiline ? 'vertical' : undefined,
      padding: 0,
      lineHeight: multiline ? 'var(--lh-normal)' : undefined
    }
  })), suffix ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-subtle)',
      whiteSpace: 'nowrap'
    }
  }, suffix) : null);
}
Object.assign(__ds_scope, { Input });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/forms/Input.jsx", error: String((e && e.message) || e) }); }

// components/forms/Radio.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Radio({
  checked = false,
  label,
  description,
  name,
  value,
  disabled = false,
  onChange,
  style,
  ...rest
}) {
  return /*#__PURE__*/React.createElement("label", _extends({}, rest, {
    style: {
      display: 'inline-flex',
      alignItems: description ? 'flex-start' : 'center',
      gap: 10,
      cursor: disabled ? 'not-allowed' : 'pointer',
      opacity: disabled ? 0.45 : 1,
      minHeight: 'var(--space-8)',
      ...style
    }
  }), /*#__PURE__*/React.createElement("input", {
    type: "radio",
    name: name,
    value: value,
    checked: checked,
    disabled: disabled,
    onChange: onChange,
    style: {
      position: 'absolute',
      opacity: 0,
      width: 0,
      height: 0
    }
  }), /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: 22,
      height: 22,
      flex: '0 0 auto',
      marginTop: description ? 1 : 0,
      borderRadius: 'var(--radius-full)',
      background: 'var(--surface-card)',
      border: '2px solid ' + (checked ? 'var(--accent)' : 'var(--border-strong)'),
      boxShadow: checked ? 'none' : 'var(--inset-field)',
      transition: 'border-color var(--dur-fast) var(--ease-standard)'
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      width: 11,
      height: 11,
      borderRadius: 999,
      background: 'var(--accent)',
      transform: checked ? 'scale(1)' : 'scale(0)',
      transition: 'transform var(--dur-fast) var(--ease-spring)'
    }
  })), label ? /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'grid',
      gap: 2
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-subhead)',
      color: 'var(--text-body)'
    }
  }, label), description ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-subtle)'
    }
  }, description) : null) : null);
}
Object.assign(__ds_scope, { Radio });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/forms/Radio.jsx", error: String((e && e.message) || e) }); }

// components/forms/Select.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Select({
  options = [],
  invalid = false,
  size = 'md',
  placeholder,
  style,
  ...rest
}) {
  const h = size === 'sm' ? 'var(--control-h-sm)' : size === 'lg' ? 'var(--control-h-lg)' : 'var(--control-h-md)';
  return /*#__PURE__*/React.createElement("div", {
    style: {
      position: 'relative',
      display: 'flex',
      alignItems: 'center',
      ...style
    }
  }, /*#__PURE__*/React.createElement("select", _extends({}, rest, {
    style: {
      appearance: 'none',
      WebkitAppearance: 'none',
      width: '100%',
      height: h,
      padding: '0 38px 0 14px',
      background: 'var(--surface-card)',
      border: '1px solid ' + (invalid ? 'var(--danger)' : 'var(--border-default)'),
      borderRadius: 'var(--radius-field)',
      boxShadow: 'var(--inset-field)',
      font: size === 'sm' ? 'var(--type-footnote)' : 'var(--type-subhead)',
      color: 'var(--text-body)',
      cursor: 'pointer'
    }
  }), placeholder ? /*#__PURE__*/React.createElement("option", {
    value: ""
  }, placeholder) : null, options.map(o => {
    const opt = typeof o === 'string' ? {
      value: o,
      label: o
    } : o;
    return /*#__PURE__*/React.createElement("option", {
      key: opt.value,
      value: opt.value
    }, opt.label);
  })), /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: "chevron-down",
    size: 18,
    color: "var(--text-subtle)",
    style: {
      position: 'absolute',
      right: 12,
      pointerEvents: 'none'
    }
  }));
}
Object.assign(__ds_scope, { Select });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/forms/Select.jsx", error: String((e && e.message) || e) }); }

// components/forms/Slider.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Slider({
  value = 50,
  min = 0,
  max = 100,
  step = 1,
  onChange,
  label,
  valueLabel,
  disabled = false,
  style,
  ...rest
}) {
  const pct = (value - min) / (max - min) * 100;
  return /*#__PURE__*/React.createElement("div", _extends({}, rest, {
    style: {
      display: 'grid',
      gap: 8,
      ...style
    }
  }), label || valueLabel ? /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'baseline'
    }
  }, label ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-label)',
      color: 'var(--text-body)'
    }
  }, label) : /*#__PURE__*/React.createElement("span", null), valueLabel ? /*#__PURE__*/React.createElement("span", {
    className: "tabular",
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-muted)'
    }
  }, valueLabel) : null) : null, /*#__PURE__*/React.createElement("input", {
    type: "range",
    value: value,
    min: min,
    max: max,
    step: step,
    disabled: disabled,
    onChange: onChange,
    style: {
      WebkitAppearance: 'none',
      appearance: 'none',
      width: '100%',
      height: 28,
      background: 'transparent',
      cursor: disabled ? 'not-allowed' : 'pointer',
      opacity: disabled ? 0.45 : 1,
      '--pui-pct': pct + '%'
    }
  }), /*#__PURE__*/React.createElement("style", null, `
        input[type=range]::-webkit-slider-runnable-track{height:6px;border-radius:999px;background:linear-gradient(to right,var(--accent) var(--pui-pct),var(--track) var(--pui-pct))}
        input[type=range]::-webkit-slider-thumb{-webkit-appearance:none;width:24px;height:24px;margin-top:-9px;border-radius:999px;background:#fff;box-shadow:0 2px 6px rgba(16,19,26,.24);border:1px solid var(--border-subtle)}
        input[type=range]::-moz-range-track{height:6px;border-radius:999px;background:var(--track)}
        input[type=range]::-moz-range-progress{height:6px;border-radius:999px;background:var(--accent)}
        input[type=range]::-moz-range-thumb{width:22px;height:22px;border:1px solid var(--border-subtle);border-radius:999px;background:#fff}
      `));
}
Object.assign(__ds_scope, { Slider });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/forms/Slider.jsx", error: String((e && e.message) || e) }); }

// components/forms/Switch.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
const DIM = {
  sm: [38, 22, 18],
  md: [51, 31, 27]
};
function Switch({
  checked = false,
  onChange,
  label,
  description,
  size = 'md',
  disabled = false,
  style,
  ...rest
}) {
  const [w, h, knob] = DIM[size] || DIM.md;
  return /*#__PURE__*/React.createElement("label", _extends({}, rest, {
    style: {
      display: 'inline-flex',
      alignItems: description ? 'flex-start' : 'center',
      justifyContent: label ? 'space-between' : 'flex-start',
      gap: 14,
      cursor: disabled ? 'not-allowed' : 'pointer',
      opacity: disabled ? 0.45 : 1,
      ...style
    }
  }), label ? /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'grid',
      gap: 2
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-subhead)',
      color: 'var(--text-body)'
    }
  }, label), description ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-subtle)'
    }
  }, description) : null) : null, /*#__PURE__*/React.createElement("input", {
    type: "checkbox",
    role: "switch",
    checked: checked,
    disabled: disabled,
    onChange: onChange,
    style: {
      position: 'absolute',
      opacity: 0,
      width: 0,
      height: 0
    }
  }), /*#__PURE__*/React.createElement("span", {
    style: {
      position: 'relative',
      flex: '0 0 auto',
      width: w,
      height: h,
      borderRadius: 'var(--radius-full)',
      background: checked ? 'var(--success)' : 'var(--border-strong)',
      transition: 'background var(--dur-base) var(--ease-standard)'
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      position: 'absolute',
      top: (h - knob) / 2,
      left: checked ? w - knob - (h - knob) / 2 : (h - knob) / 2,
      width: knob,
      height: knob,
      borderRadius: 999,
      background: '#fff',
      boxShadow: '0 2px 5px rgba(16,19,26,.22)',
      transition: 'left var(--dur-base) var(--ease-emphasized)'
    }
  })));
}
Object.assign(__ds_scope, { Switch });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/forms/Switch.jsx", error: String((e && e.message) || e) }); }

// components/navigation/ListRow.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function ListRow({
  title,
  subtitle,
  leading,
  trailing,
  chevron = false,
  onClick,
  divider = true,
  style,
  ...rest
}) {
  const clickable = !!onClick;
  return /*#__PURE__*/React.createElement("div", _extends({}, rest, {
    onClick: onClick,
    role: clickable ? 'button' : undefined,
    tabIndex: clickable ? 0 : undefined,
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 'var(--space-5)',
      minHeight: 'var(--hit-min)',
      padding: 'var(--space-5) var(--space-6)',
      borderBottom: divider ? '1px solid var(--border-subtle)' : 'none',
      cursor: clickable ? 'pointer' : 'default',
      transition: 'background var(--dur-fast) var(--ease-standard)',
      ...style
    },
    onPointerEnter: ev => {
      if (clickable) ev.currentTarget.style.background = 'var(--surface-hover)';
    },
    onPointerLeave: ev => {
      if (clickable) ev.currentTarget.style.background = 'transparent';
    }
  }), leading, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 2,
      minWidth: 0,
      flex: 1
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-subhead)',
      fontWeight: 'var(--weight-semibold)',
      color: 'var(--text-heading)',
      overflow: 'hidden',
      textOverflow: 'ellipsis',
      whiteSpace: 'nowrap'
    }
  }, title), subtitle ? /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-muted)',
      overflow: 'hidden',
      textOverflow: 'ellipsis',
      whiteSpace: 'nowrap'
    }
  }, subtitle) : null), trailing, chevron ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
    name: "chevron-right",
    size: 18,
    color: "var(--text-subtle)"
  }) : null);
}
Object.assign(__ds_scope, { ListRow });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/navigation/ListRow.jsx", error: String((e && e.message) || e) }); }

// components/navigation/SegmentedControl.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function SegmentedControl({
  items = [],
  value,
  onChange,
  size = 'md',
  block = false,
  style,
  ...rest
}) {
  const h = size === 'sm' ? 28 : 34;
  return /*#__PURE__*/React.createElement("div", _extends({
    role: "tablist"
  }, rest, {
    style: {
      display: block ? 'flex' : 'inline-flex',
      width: block ? '100%' : undefined,
      padding: 2,
      gap: 2,
      background: 'var(--surface-sunken)',
      borderRadius: 'var(--radius-full)',
      ...style
    }
  }), items.map(raw => {
    const it = typeof raw === 'string' ? {
      value: raw,
      label: raw
    } : raw;
    const on = it.value === value;
    return /*#__PURE__*/React.createElement("button", {
      key: it.value,
      role: "tab",
      "aria-selected": on,
      onClick: () => onChange && onChange(it.value),
      style: {
        flex: block ? 1 : undefined,
        height: h,
        padding: '0 14px',
        border: 'none',
        cursor: 'pointer',
        borderRadius: 'var(--radius-full)',
        background: on ? 'var(--surface-card)' : 'transparent',
        boxShadow: on ? 'var(--shadow-1)' : 'none',
        color: on ? 'var(--text-heading)' : 'var(--text-muted)',
        font: `var(--weight-${on ? 'semibold' : 'medium'}) var(--text-footnote)/1 var(--font-core)`,
        transition: 'background var(--dur-fast) var(--ease-standard), color var(--dur-fast) var(--ease-standard)',
        whiteSpace: 'nowrap'
      }
    }, it.label);
  }));
}
Object.assign(__ds_scope, { SegmentedControl });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/navigation/SegmentedControl.jsx", error: String((e && e.message) || e) }); }

// components/navigation/Tabs.jsx
try { (() => {
function _extends() { return _extends = Object.assign ? Object.assign.bind() : function (n) { for (var e = 1; e < arguments.length; e++) { var t = arguments[e]; for (var r in t) ({}).hasOwnProperty.call(t, r) && (n[r] = t[r]); } return n; }, _extends.apply(null, arguments); }
function Tabs({
  items = [],
  value,
  onChange,
  style,
  ...rest
}) {
  return /*#__PURE__*/React.createElement("div", _extends({
    role: "tablist"
  }, rest, {
    style: {
      display: 'flex',
      gap: 'var(--space-7)',
      borderBottom: '1px solid var(--border-subtle)',
      ...style
    }
  }), items.map(raw => {
    const it = typeof raw === 'string' ? {
      value: raw,
      label: raw
    } : raw;
    const on = it.value === value;
    return /*#__PURE__*/React.createElement("button", {
      key: it.value,
      role: "tab",
      "aria-selected": on,
      onClick: () => onChange && onChange(it.value),
      style: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: 6,
        position: 'relative',
        padding: '0 2px 12px',
        border: 'none',
        background: 'transparent',
        cursor: 'pointer',
        font: `var(--weight-${on ? 'semibold' : 'medium'}) var(--text-subhead)/1 var(--font-core)`,
        color: on ? 'var(--text-heading)' : 'var(--text-subtle)',
        transition: 'color var(--dur-fast) var(--ease-standard)'
      }
    }, it.icon ? /*#__PURE__*/React.createElement(__ds_scope.Icon, {
      name: it.icon,
      size: 16
    }) : null, it.label, it.count != null ? /*#__PURE__*/React.createElement("span", {
      className: "tabular",
      style: {
        marginLeft: 2,
        padding: '1px 7px',
        borderRadius: 999,
        background: on ? 'var(--accent-soft)' : 'var(--surface-sunken)',
        color: on ? 'var(--accent-on-soft)' : 'var(--text-subtle)',
        font: 'var(--type-caption)'
      }
    }, it.count) : null, /*#__PURE__*/React.createElement("span", {
      style: {
        position: 'absolute',
        left: 0,
        right: 0,
        bottom: -1,
        height: 3,
        borderRadius: '3px 3px 0 0',
        background: 'var(--accent)',
        opacity: on ? 1 : 0,
        transition: 'opacity var(--dur-base) var(--ease-standard)'
      }
    }));
  }));
}
Object.assign(__ds_scope, { Tabs });
})(); } catch (e) { __ds_ns.__errors.push({ path: "components/navigation/Tabs.jsx", error: String((e && e.message) || e) }); }

// ui_kits/dashboard/Chrome.jsx
try { (() => {
const {
  Icon,
  IconButton,
  Button,
  Badge
} = window.PersonalUIDesignSystem_06a1d8;
const NAV = [{
  id: 'today',
  label: 'Today',
  icon: 'house'
}, {
  id: 'habits',
  label: 'Habits',
  icon: 'target'
}, {
  id: 'health',
  label: 'Health',
  icon: 'activity'
}, {
  id: 'money',
  label: 'Money',
  icon: 'wallet'
}, {
  id: 'notes',
  label: 'Notes',
  icon: 'notebook-pen'
}];
function Sidebar({
  view,
  onView,
  theme,
  onTheme
}) {
  return /*#__PURE__*/React.createElement("aside", {
    style: {
      width: 'var(--sidebar-w)',
      flex: '0 0 auto',
      padding: 'var(--space-6)',
      display: 'flex',
      flexDirection: 'column',
      gap: 'var(--space-8)',
      borderRight: '1px solid var(--border-subtle)',
      background: 'var(--surface-card)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 10,
      padding: '4px 6px'
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      width: 24,
      height: 24,
      borderRadius: 999,
      background: 'var(--accent)',
      boxShadow: 'var(--shadow-accent)'
    }
  }), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--weight-extrabold) 18px/1 var(--font-display)',
      letterSpacing: '-.03em',
      color: 'var(--text-heading)'
    }
  }, "Personal UI")), /*#__PURE__*/React.createElement("nav", {
    style: {
      display: 'grid',
      gap: 2
    }
  }, NAV.map(n => {
    const on = n.id === view;
    return /*#__PURE__*/React.createElement("button", {
      key: n.id,
      onClick: () => onView(n.id),
      style: {
        display: 'flex',
        alignItems: 'center',
        gap: 12,
        height: 42,
        padding: '0 12px',
        border: 'none',
        cursor: 'pointer',
        borderRadius: 'var(--radius-full)',
        background: on ? 'var(--accent-soft)' : 'transparent',
        color: on ? 'var(--accent-on-soft)' : 'var(--text-muted)',
        font: `var(--weight-${on ? 'semibold' : 'medium'}) var(--text-subhead)/1 var(--font-core)`,
        transition: 'background var(--dur-fast) var(--ease-standard)'
      }
    }, /*#__PURE__*/React.createElement(Icon, {
      name: n.icon,
      size: 20
    }), n.label);
  })), /*#__PURE__*/React.createElement("div", {
    style: {
      marginTop: 'auto',
      display: 'grid',
      gap: 'var(--space-5)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      padding: 'var(--space-5)',
      borderRadius: 'var(--radius-tile)',
      background: 'var(--surface-sunken)',
      display: 'grid',
      gap: 6
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-caption)',
      letterSpacing: 'var(--tracking-caps)',
      textTransform: 'uppercase',
      color: 'var(--text-subtle)'
    }
  }, "Sync"), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-muted)'
    }
  }, "Apple Health \xB7 4 min ago")), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 10
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      width: 32,
      height: 32,
      borderRadius: 999,
      background: 'var(--info-soft)',
      color: 'var(--info-on-soft)',
      display: 'grid',
      placeItems: 'center',
      font: 'var(--weight-bold) 13px/1 var(--font-core)'
    }
  }, "R"), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      fontWeight: 'var(--weight-semibold)',
      color: 'var(--text-body)'
    }
  }, "Ranjan")), /*#__PURE__*/React.createElement(IconButton, {
    icon: theme === 'dark' ? 'sun' : 'moon',
    label: "Toggle theme",
    size: "sm",
    onClick: onTheme
  }))));
}
function TopBar({
  title,
  subtitle,
  onAdd
}) {
  return /*#__PURE__*/React.createElement("header", {
    style: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      gap: 'var(--space-6)',
      padding: '0 0 var(--space-8)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 4
    }
  }, /*#__PURE__*/React.createElement("h2", {
    style: {
      font: 'var(--type-title-1)',
      letterSpacing: 'var(--tracking-title)',
      color: 'var(--text-heading)'
    }
  }, title), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-subhead)',
      color: 'var(--text-muted)'
    }
  }, subtitle)), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 'var(--gap-inline)'
    }
  }, /*#__PURE__*/React.createElement(IconButton, {
    icon: "search",
    label: "Search",
    variant: "outline"
  }), /*#__PURE__*/React.createElement(IconButton, {
    icon: "bell",
    label: "Notifications",
    variant: "outline"
  }), /*#__PURE__*/React.createElement(Button, {
    icon: "plus",
    onClick: onAdd
  }, "Quick add")));
}
Object.assign(window, {
  Sidebar,
  TopBar,
  NAV
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/dashboard/Chrome.jsx", error: String((e && e.message) || e) }); }

// ui_kits/dashboard/HealthView.jsx
try { (() => {
const {
  Card,
  CardHeader,
  Stat,
  ProgressRing,
  ListRow,
  Badge,
  Slider,
  Field,
  Select,
  Tabs
} = window.PersonalUIDesignSystem_06a1d8;
const SLEEP = [7.2, 6.1, 6.4, 8.0, 5.5, 7.8, 6.4];
function HealthView({
  target,
  onTarget
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 'var(--gap-group)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gridTemplateColumns: 'repeat(3,minmax(0,1fr))',
      gap: 'var(--gap-group)'
    }
  }, /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Stat, {
    label: "Resting HR",
    value: "58",
    unit: "bpm",
    delta: "-2",
    deltaTone: "success",
    caption: "vs 30-day",
    icon: "heart-pulse"
  })), /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Stat, {
    label: "Weight",
    value: "72.4",
    unit: "kg",
    delta: "-0.6",
    deltaTone: "success",
    caption: "this month",
    icon: "scale"
  })), /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Stat, {
    label: "Active",
    value: "41",
    unit: "min",
    delta: "+9",
    caption: "vs yesterday",
    icon: "activity"
  }))), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gridTemplateColumns: 'minmax(0,1fr) minmax(0,1fr)',
      gap: 'var(--gap-group)',
      alignItems: 'start'
    }
  }, /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(CardHeader, {
    title: "Sleep",
    subtitle: "Hours per night, last week"
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'flex-end',
      gap: 10,
      height: 120
    }
  }, SLEEP.map((v, i) => /*#__PURE__*/React.createElement("div", {
    key: i,
    style: {
      flex: 1,
      display: 'grid',
      gap: 6,
      justifyItems: 'center'
    }
  }, /*#__PURE__*/React.createElement("span", {
    className: "tabular",
    style: {
      font: 'var(--type-caption)',
      color: 'var(--text-subtle)'
    }
  }, v), /*#__PURE__*/React.createElement("div", {
    style: {
      width: '100%',
      height: 84,
      display: 'flex',
      alignItems: 'flex-end',
      background: 'var(--track)',
      borderRadius: 'var(--radius-sm)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: '100%',
      height: v / 9 * 100 + '%',
      background: v >= 7 ? 'var(--chart-2)' : 'var(--chart-4)',
      borderRadius: 'var(--radius-sm)'
    }
  })), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-caption)',
      color: 'var(--text-subtle)'
    }
  }, ['M', 'T', 'W', 'T', 'F', 'S', 'S'][i]))))), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 'var(--gap-group)'
    }
  }, /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(CardHeader, {
    title: "Daily goals",
    subtitle: "Applies immediately"
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 'var(--gap-group)'
    }
  }, /*#__PURE__*/React.createElement(Slider, {
    label: "Active minutes",
    valueLabel: target + ' min',
    value: target,
    max: 120,
    step: 5,
    onChange: e => onTarget(+e.target.value)
  }), /*#__PURE__*/React.createElement(Field, {
    label: "Sleep window"
  }, /*#__PURE__*/React.createElement(Select, {
    options: ['22:30 – 06:30', '23:00 – 07:00', '23:30 – 07:30']
  })))), /*#__PURE__*/React.createElement(Card, {
    padding: "none"
  }, /*#__PURE__*/React.createElement(ListRow, {
    leading: /*#__PURE__*/React.createElement(ProgressRing, {
      value: 100,
      size: 34,
      tone: "success"
    }),
    title: "Apple Health",
    subtitle: "Steps, sleep, heart rate",
    trailing: /*#__PURE__*/React.createElement(Badge, {
      tone: "success",
      dot: true
    }, "Connected")
  }), /*#__PURE__*/React.createElement(ListRow, {
    leading: /*#__PURE__*/React.createElement(ProgressRing, {
      value: 0,
      size: 34,
      tone: "danger"
    }),
    title: "Strava",
    subtitle: "Runs and rides",
    trailing: /*#__PURE__*/React.createElement(Badge, {
      tone: "neutral"
    }, "Not linked"),
    divider: false
  })))));
}
Object.assign(window, {
  HealthView
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/dashboard/HealthView.jsx", error: String((e && e.message) || e) }); }

// ui_kits/dashboard/TodayView.jsx
try { (() => {
const {
  Card,
  CardHeader,
  Stat,
  ProgressRing,
  ListRow,
  Badge,
  Button,
  IconButton,
  SegmentedControl,
  Tag,
  Tooltip,
  Icon
} = window.PersonalUIDesignSystem_06a1d8;
const HABITS = [{
  name: 'Morning walk',
  meta: '5 day streak',
  pct: 100,
  tone: 'success',
  badge: ['success', 'Held']
}, {
  name: 'Read 20 pages',
  meta: 'Missed yesterday',
  pct: 40,
  tone: 'warning',
  badge: ['warning', 'At risk']
}, {
  name: 'No screens after 10',
  meta: '12 day streak',
  pct: 100,
  tone: 'success',
  badge: ['success', 'Held']
}, {
  name: 'Strength session',
  meta: '2 of 4 this week',
  pct: 50,
  tone: 'accent',
  badge: ['neutral', 'Planned']
}];
const WEEK = [62, 78, 95, 40, 88, 71, 55];
function Sparkbars({
  data,
  tone = 'var(--chart-1)'
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'flex-end',
      gap: 8,
      height: 92
    }
  }, data.map((v, i) => /*#__PURE__*/React.createElement("div", {
    key: i,
    style: {
      flex: 1,
      display: 'grid',
      gap: 6,
      justifyItems: 'center'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: '100%',
      height: 72,
      display: 'flex',
      alignItems: 'flex-end',
      background: 'var(--track)',
      borderRadius: 'var(--radius-sm)',
      overflow: 'hidden'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: '100%',
      height: v + '%',
      background: i === 4 ? tone : 'var(--accent-soft-border)',
      borderRadius: 'var(--radius-sm)',
      transition: 'height var(--dur-slow) var(--ease-emphasized)'
    }
  })), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-caption)',
      color: 'var(--text-subtle)'
    }
  }, ['M', 'T', 'W', 'T', 'F', 'S', 'S'][i]))));
}
function TodayView({
  done,
  onToggle,
  range,
  onRange
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 'var(--gap-group)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gridTemplateColumns: 'repeat(4,minmax(0,1fr))',
      gap: 'var(--gap-group)'
    }
  }, /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Stat, {
    label: "Steps",
    value: "8,412",
    delta: "+12%",
    caption: "vs last week",
    icon: "footprints"
  })), /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Stat, {
    label: "Sleep",
    value: "6.4",
    unit: "hrs",
    delta: "-0.8",
    caption: "vs target",
    icon: "moon"
  })), /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Stat, {
    label: "Focus",
    value: "3",
    unit: "/4",
    deltaTone: "neutral",
    caption: "sessions logged",
    icon: "timer"
  })), /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Stat, {
    label: "Spend",
    value: "$42",
    delta: "-18%",
    deltaTone: "success",
    caption: "under daily cap",
    icon: "wallet"
  }))), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gridTemplateColumns: 'minmax(0,1.5fr) minmax(0,1fr)',
      gap: 'var(--gap-group)',
      alignItems: 'start'
    }
  }, /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(CardHeader, {
    title: "Today's habits",
    subtitle: `${done.length} of ${HABITS.length} complete`,
    action: /*#__PURE__*/React.createElement(SegmentedControl, {
      size: "sm",
      value: range,
      onChange: onRange,
      items: ['Today', 'Week']
    })
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      margin: '0 calc(var(--pad-card) * -1) calc(var(--pad-card) * -1)'
    }
  }, HABITS.map((h, i) => {
    const complete = done.includes(h.name);
    return /*#__PURE__*/React.createElement(ListRow, {
      key: h.name,
      divider: i < HABITS.length - 1,
      leading: /*#__PURE__*/React.createElement(ProgressRing, {
        value: complete ? 100 : h.pct,
        size: 34,
        tone: complete ? 'success' : h.tone
      }),
      title: h.name,
      subtitle: h.meta,
      trailing: /*#__PURE__*/React.createElement("div", {
        style: {
          display: 'flex',
          alignItems: 'center',
          gap: 'var(--gap-inline)'
        }
      }, /*#__PURE__*/React.createElement(Badge, {
        tone: complete ? 'success' : h.badge[0],
        dot: complete || h.badge[0] === 'success'
      }, complete ? 'Held' : h.badge[1]), /*#__PURE__*/React.createElement(Button, {
        size: "sm",
        variant: complete ? 'soft' : 'secondary',
        tone: "success",
        icon: complete ? 'check' : undefined,
        onClick: () => onToggle(h.name)
      }, complete ? 'Done' : 'Log')),
      style: {
        padding: 'var(--space-5) var(--pad-card)'
      }
    });
  }))), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 'var(--gap-group)'
    }
  }, /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(CardHeader, {
    title: "Consistency",
    subtitle: "Last 7 days",
    action: /*#__PURE__*/React.createElement(Tooltip, {
      label: "Rolling 7-day average"
    }, /*#__PURE__*/React.createElement(Icon, {
      name: "info",
      size: 16,
      color: "var(--text-subtle)"
    }))
  }), /*#__PURE__*/React.createElement(Sparkbars, {
    data: WEEK
  })), /*#__PURE__*/React.createElement(Card, {
    tint: "accent"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      gap: 'var(--space-5)',
      alignItems: 'center'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      background: 'var(--surface-card)',
      borderRadius: 999,
      padding: 3
    }
  }, /*#__PURE__*/React.createElement(ProgressRing, {
    value: 78,
    size: 72,
    label: "78%"
  })), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 4
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-title-3)',
      color: 'var(--accent-on-soft)'
    }
  }, "Best week yet"), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--accent-on-soft)',
      opacity: .85
    }
  }, "Nine days without breaking a streak.")))), /*#__PURE__*/React.createElement(Card, {
    padding: "sm"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      gap: 'var(--gap-inline)',
      flexWrap: 'wrap'
    }
  }, /*#__PURE__*/React.createElement(Tag, {
    color: "var(--chart-2)"
  }, "Health"), /*#__PURE__*/React.createElement(Tag, {
    color: "var(--chart-3)"
  }, "Focus"), /*#__PURE__*/React.createElement(Tag, {
    color: "var(--chart-4)"
  }, "Money"), /*#__PURE__*/React.createElement(Tag, {
    selected: true
  }, "All"))))));
}
Object.assign(window, {
  TodayView,
  Sparkbars,
  HABITS
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/dashboard/TodayView.jsx", error: String((e && e.message) || e) }); }

// ui_kits/habits_app/Phone.jsx
try { (() => {
const {
  Icon
} = window.PersonalUIDesignSystem_06a1d8;
function Phone({
  children,
  theme = 'light'
}) {
  return /*#__PURE__*/React.createElement("div", {
    "data-theme": theme,
    style: {
      width: 390,
      height: 780,
      flex: '0 0 auto',
      position: 'relative',
      background: 'var(--bg-app)',
      borderRadius: 44,
      overflow: 'hidden',
      border: '1px solid var(--border-default)',
      boxShadow: 'var(--shadow-4)',
      display: 'flex',
      flexDirection: 'column',
      color: 'var(--text-body)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      height: 52,
      flex: '0 0 auto',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '0 26px',
      font: 'var(--weight-semibold) 14px/1 var(--font-core)',
      color: 'var(--text-heading)'
    }
  }, /*#__PURE__*/React.createElement("span", {
    className: "tabular"
  }, "9:41"), /*#__PURE__*/React.createElement("span", {
    style: {
      display: 'flex',
      gap: 6,
      alignItems: 'center'
    }
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "signal",
    size: 15
  }), /*#__PURE__*/React.createElement(Icon, {
    name: "wifi",
    size: 15
  }), /*#__PURE__*/React.createElement(Icon, {
    name: "battery-full",
    size: 17
  }))), children);
}
const TABS = [{
  id: 'today',
  label: 'Today',
  icon: 'circle-check-big'
}, {
  id: 'stats',
  label: 'Stats',
  icon: 'chart-column'
}, {
  id: 'add',
  label: '',
  icon: 'plus'
}, {
  id: 'log',
  label: 'Log',
  icon: 'book-open'
}, {
  id: 'you',
  label: 'You',
  icon: 'user-round'
}];
function TabBar({
  value,
  onChange,
  onAdd
}) {
  return /*#__PURE__*/React.createElement("nav", {
    style: {
      flex: '0 0 auto',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-around',
      padding: '8px 12px 22px',
      background: 'color-mix(in oklab, var(--surface-card) 82%, transparent)',
      backdropFilter: 'var(--blur-sheet)',
      WebkitBackdropFilter: 'var(--blur-sheet)',
      borderTop: '1px solid var(--border-subtle)'
    }
  }, TABS.map(t => t.id === 'add' ? /*#__PURE__*/React.createElement("button", {
    key: "add",
    onClick: onAdd,
    "aria-label": "Add entry",
    style: {
      width: 52,
      height: 52,
      marginTop: -22,
      borderRadius: 999,
      border: 'none',
      cursor: 'pointer',
      background: 'var(--accent)',
      color: '#fff',
      boxShadow: 'var(--shadow-accent)',
      display: 'grid',
      placeItems: 'center'
    }
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "plus",
    size: 26
  })) : /*#__PURE__*/React.createElement("button", {
    key: t.id,
    onClick: () => onChange(t.id),
    style: {
      border: 'none',
      background: 'transparent',
      cursor: 'pointer',
      display: 'grid',
      gap: 3,
      justifyItems: 'center',
      width: 64,
      color: value === t.id ? 'var(--accent)' : 'var(--text-subtle)',
      font: `var(--weight-${value === t.id ? 'semibold' : 'medium'}) var(--text-caption-2)/1 var(--font-core)`
    }
  }, /*#__PURE__*/React.createElement(Icon, {
    name: t.icon,
    size: 23
  }), t.label)));
}
Object.assign(window, {
  Phone,
  TabBar
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/habits_app/Phone.jsx", error: String((e && e.message) || e) }); }

// ui_kits/habits_app/Screens.jsx
try { (() => {
const {
  Card,
  CardHeader,
  ListRow,
  ProgressRing,
  Badge,
  Button,
  IconButton,
  SegmentedControl,
  Stat,
  Switch,
  Tag,
  Icon,
  Field,
  Input,
  Select,
  Checkbox,
  Slider
} = window.PersonalUIDesignSystem_06a1d8;
const SCROLL = {
  flex: 1,
  minHeight: 0,
  overflow: 'auto',
  padding: '0 var(--space-6) var(--space-6)',
  display: 'grid',
  gap: 'var(--gap-group)',
  alignContent: 'start'
};
function ScreenHead({
  eyebrow,
  title,
  action
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'flex-start',
      justifyContent: 'space-between',
      padding: 'var(--space-4) var(--space-6) var(--space-6)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 2
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-caption)',
      letterSpacing: 'var(--tracking-caps)',
      textTransform: 'uppercase',
      color: 'var(--text-subtle)'
    }
  }, eyebrow), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-title-1)',
      letterSpacing: 'var(--tracking-title)',
      color: 'var(--text-heading)'
    }
  }, title)), action);
}
function TodayScreen({
  habits,
  done,
  onToggle
}) {
  const pct = Math.round(done.length / habits.length * 100);
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(ScreenHead, {
    eyebrow: "Thursday 20 Sep",
    title: "Today",
    action: /*#__PURE__*/React.createElement(IconButton, {
      icon: "bell",
      label: "Reminders",
      variant: "outline"
    })
  }), /*#__PURE__*/React.createElement("div", {
    style: SCROLL
  }, /*#__PURE__*/React.createElement(Card, {
    tint: "accent",
    padding: "lg"
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 'var(--space-6)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      background: 'var(--surface-card)',
      borderRadius: 999,
      padding: 3
    }
  }, /*#__PURE__*/React.createElement(ProgressRing, {
    value: pct,
    size: 86,
    label: pct + '%'
  })), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 4
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-title-3)',
      color: 'var(--accent-on-soft)'
    }
  }, done.length, " of ", habits.length, " held"), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--accent-on-soft)',
      opacity: .85
    }
  }, "Keep the walk and you hit ten days.")))), /*#__PURE__*/React.createElement(Card, {
    padding: "none"
  }, habits.map((h, i) => {
    const complete = done.includes(h.name);
    return /*#__PURE__*/React.createElement(ListRow, {
      key: h.name,
      divider: i < habits.length - 1,
      leading: /*#__PURE__*/React.createElement(ProgressRing, {
        value: complete ? 100 : h.pct,
        size: 36,
        tone: complete ? 'success' : h.tone
      }),
      title: h.name,
      subtitle: h.meta,
      trailing: /*#__PURE__*/React.createElement(Button, {
        size: "sm",
        variant: complete ? 'soft' : 'secondary',
        tone: "success",
        icon: complete ? 'check' : undefined,
        onClick: () => onToggle(h.name)
      }, complete ? 'Done' : 'Log')
    });
  })), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      gap: 'var(--gap-inline)',
      flexWrap: 'wrap'
    }
  }, /*#__PURE__*/React.createElement(Tag, {
    color: "var(--chart-2)"
  }, "Health"), /*#__PURE__*/React.createElement(Tag, {
    color: "var(--chart-3)"
  }, "Focus"), /*#__PURE__*/React.createElement(Tag, {
    selected: true
  }, "All"))));
}
function StatsScreen({
  range,
  onRange
}) {
  const data = [62, 78, 95, 40, 88, 71, 55];
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(ScreenHead, {
    eyebrow: "Consistency",
    title: "Stats"
  }), /*#__PURE__*/React.createElement("div", {
    style: SCROLL
  }, /*#__PURE__*/React.createElement(SegmentedControl, {
    block: true,
    value: range,
    onChange: onRange,
    items: ['Week', 'Month', 'Year']
  }), /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(CardHeader, {
    title: "Completion",
    subtitle: range === 'Week' ? 'Last 7 days' : 'Rolling ' + range.toLowerCase()
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'flex-end',
      gap: 8,
      height: 112
    }
  }, data.map((v, i) => /*#__PURE__*/React.createElement("div", {
    key: i,
    style: {
      flex: 1,
      display: 'grid',
      gap: 6,
      justifyItems: 'center'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: '100%',
      height: 88,
      display: 'flex',
      alignItems: 'flex-end',
      background: 'var(--track)',
      borderRadius: 'var(--radius-sm)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      width: '100%',
      height: v + '%',
      background: i === 4 ? 'var(--chart-1)' : 'var(--accent-soft-border)',
      borderRadius: 'var(--radius-sm)'
    }
  })), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-caption)',
      color: 'var(--text-subtle)'
    }
  }, ['M', 'T', 'W', 'T', 'F', 'S', 'S'][i]))))), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gridTemplateColumns: '1fr 1fr',
      gap: 'var(--gap-stack)'
    }
  }, /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Stat, {
    label: "Longest streak",
    value: "18",
    unit: "days",
    deltaTone: "neutral",
    caption: "Morning walk"
  })), /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Stat, {
    label: "Held this month",
    value: "82",
    unit: "%",
    delta: "+6%",
    caption: "vs August"
  })))));
}
function LogScreen() {
  const entries = [['Today', [['Morning walk', '06:40 · 2.4 km', 'success'], ['No screens after 10', 'Held overnight', 'success']]], ['Yesterday', [['Strength session', '40 min · upper', 'success'], ['Read 20 pages', 'Missed', 'danger']]]];
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(ScreenHead, {
    eyebrow: "History",
    title: "Log",
    action: /*#__PURE__*/React.createElement(IconButton, {
      icon: "calendar",
      label: "Pick a date",
      variant: "outline"
    })
  }), /*#__PURE__*/React.createElement("div", {
    style: SCROLL
  }, entries.map(([day, rows]) => /*#__PURE__*/React.createElement("div", {
    key: day,
    style: {
      display: 'grid',
      gap: 'var(--gap-stack)'
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-caption)',
      letterSpacing: 'var(--tracking-caps)',
      textTransform: 'uppercase',
      color: 'var(--text-subtle)'
    }
  }, day), /*#__PURE__*/React.createElement(Card, {
    padding: "none"
  }, rows.map(([t, s, tone], i) => /*#__PURE__*/React.createElement(ListRow, {
    key: t,
    divider: i < rows.length - 1,
    title: t,
    subtitle: s,
    leading: /*#__PURE__*/React.createElement("span", {
      style: {
        width: 34,
        height: 34,
        borderRadius: 999,
        display: 'grid',
        placeItems: 'center',
        background: tone === 'success' ? 'var(--success-soft)' : 'var(--danger-soft)',
        color: tone === 'success' ? 'var(--success)' : 'var(--danger)'
      }
    }, /*#__PURE__*/React.createElement(Icon, {
      name: tone === 'success' ? 'check' : 'x',
      size: 17
    })),
    trailing: /*#__PURE__*/React.createElement(Badge, {
      tone: tone,
      dot: true
    }, tone === 'success' ? 'Held' : 'Broken')
  })))))));
}
function YouScreen({
  theme,
  onTheme,
  reminders,
  onReminders,
  target,
  onTarget
}) {
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(ScreenHead, {
    eyebrow: "Account",
    title: "You"
  }), /*#__PURE__*/React.createElement("div", {
    style: SCROLL
  }, /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 'var(--space-5)'
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      width: 52,
      height: 52,
      borderRadius: 999,
      background: 'var(--info-soft)',
      color: 'var(--info-on-soft)',
      display: 'grid',
      placeItems: 'center',
      font: 'var(--weight-bold) 20px/1 var(--font-core)'
    }
  }, "R"), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 2
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-title-3)',
      color: 'var(--text-heading)'
    }
  }, "Ranjan"), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-muted)'
    }
  }, "12 habits \xB7 since March")))), /*#__PURE__*/React.createElement(Card, {
    padding: "none"
  }, /*#__PURE__*/React.createElement(ListRow, {
    title: "Daily reminder",
    subtitle: "8:00 every morning",
    trailing: /*#__PURE__*/React.createElement(Switch, {
      checked: reminders,
      onChange: onReminders,
      size: "sm"
    })
  }), /*#__PURE__*/React.createElement(ListRow, {
    title: "Dark appearance",
    subtitle: "Follows this switch, not the system",
    trailing: /*#__PURE__*/React.createElement(Switch, {
      checked: theme === 'dark',
      onChange: onTheme,
      size: "sm"
    })
  }), /*#__PURE__*/React.createElement(ListRow, {
    title: "Apple Health",
    subtitle: "Steps, sleep, heart rate",
    trailing: /*#__PURE__*/React.createElement(Badge, {
      tone: "success",
      dot: true
    }, "Connected"),
    chevron: true
  }), /*#__PURE__*/React.createElement(ListRow, {
    title: "Export data",
    subtitle: "CSV of every entry",
    chevron: true,
    divider: false
  })), /*#__PURE__*/React.createElement(Card, null, /*#__PURE__*/React.createElement(Slider, {
    label: "Weekly goal",
    valueLabel: target + ' days',
    value: target,
    min: 1,
    max: 7,
    onChange: e => onTarget(+e.target.value)
  }))));
}
Object.assign(window, {
  TodayScreen,
  StatsScreen,
  LogScreen,
  YouScreen,
  ScreenHead
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/habits_app/Screens.jsx", error: String((e && e.message) || e) }); }

// ui_kits/internal_tool/RunsTable.jsx
try { (() => {
const {
  Card,
  Badge,
  IconButton,
  Checkbox,
  Tooltip,
  Icon,
  Button,
  ProgressRing
} = window.PersonalUIDesignSystem_06a1d8;
const ROWS = [{
  id: 'run_4f19',
  task: 'Nightly health sync',
  owner: 'scheduler',
  status: 'Running',
  pct: 62,
  dur: '3m 12s',
  when: '2 min ago'
}, {
  id: 'run_4f18',
  task: 'Habit streak rebuild',
  owner: 'ranjan',
  status: 'Passed',
  pct: 100,
  dur: '48s',
  when: '1 hr ago'
}, {
  id: 'run_4f17',
  task: 'Budget import · Sept',
  owner: 'ranjan',
  status: 'Failed',
  pct: 100,
  dur: '12s',
  when: '3 hr ago'
}, {
  id: 'run_4f16',
  task: 'Notes reindex',
  owner: 'scheduler',
  status: 'Passed',
  pct: 100,
  dur: '2m 04s',
  when: 'Yesterday'
}, {
  id: 'run_4f15',
  task: 'Apple Health backfill',
  owner: 'ranjan',
  status: 'Queued',
  pct: 0,
  dur: '—',
  when: 'Yesterday'
}];
const TONE = {
  Running: 'accent',
  Passed: 'success',
  Failed: 'danger',
  Queued: 'neutral'
};
const TH = {
  textAlign: 'left',
  font: 'var(--type-caption)',
  letterSpacing: 'var(--tracking-caps)',
  textTransform: 'uppercase',
  color: 'var(--text-subtle)',
  padding: '0 var(--space-6) var(--space-5)',
  whiteSpace: 'nowrap'
};
const TD = {
  padding: 'var(--space-5) var(--space-6)',
  borderTop: '1px solid var(--border-subtle)',
  font: 'var(--type-subhead)',
  color: 'var(--text-body)',
  verticalAlign: 'middle'
};
function RunsTable({
  query,
  status,
  selected,
  onSelect,
  onOpen
}) {
  const rows = ROWS.filter(r => (status === 'All' || r.status === status) && (r.id + r.task + r.owner).toLowerCase().includes(query.toLowerCase()));
  const allOn = rows.length > 0 && rows.every(r => selected.includes(r.id));
  return /*#__PURE__*/React.createElement(Card, {
    padding: "none",
    style: {
      overflow: 'hidden'
    }
  }, /*#__PURE__*/React.createElement("table", {
    style: {
      width: '100%',
      borderCollapse: 'collapse'
    }
  }, /*#__PURE__*/React.createElement("thead", null, /*#__PURE__*/React.createElement("tr", null, /*#__PURE__*/React.createElement("th", {
    style: {
      ...TH,
      width: 44,
      paddingRight: 0
    }
  }, /*#__PURE__*/React.createElement(Checkbox, {
    checked: allOn,
    indeterminate: !allOn && selected.length > 0,
    onChange: () => onSelect(allOn ? [] : rows.map(r => r.id))
  })), /*#__PURE__*/React.createElement("th", {
    style: TH
  }, "Run"), /*#__PURE__*/React.createElement("th", {
    style: TH
  }, "Owner"), /*#__PURE__*/React.createElement("th", {
    style: TH
  }, "Status"), /*#__PURE__*/React.createElement("th", {
    style: TH
  }, "Duration"), /*#__PURE__*/React.createElement("th", {
    style: TH
  }, "Started"), /*#__PURE__*/React.createElement("th", {
    style: {
      ...TH,
      width: 60
    }
  }))), /*#__PURE__*/React.createElement("tbody", null, rows.map(r => {
    const on = selected.includes(r.id);
    return /*#__PURE__*/React.createElement("tr", {
      key: r.id,
      style: {
        background: on ? 'var(--surface-selected)' : 'transparent',
        transition: 'background var(--dur-fast) var(--ease-standard)'
      }
    }, /*#__PURE__*/React.createElement("td", {
      style: {
        ...TD,
        paddingRight: 0
      }
    }, /*#__PURE__*/React.createElement(Checkbox, {
      checked: on,
      onChange: () => onSelect(on ? selected.filter(x => x !== r.id) : [...selected, r.id])
    })), /*#__PURE__*/React.createElement("td", {
      style: TD
    }, /*#__PURE__*/React.createElement("div", {
      style: {
        display: 'grid',
        gap: 2
      }
    }, /*#__PURE__*/React.createElement("span", {
      style: {
        fontWeight: 'var(--weight-semibold)',
        color: 'var(--text-heading)'
      }
    }, r.task), /*#__PURE__*/React.createElement("span", {
      style: {
        font: 'var(--type-mono)',
        fontSize: 11,
        color: 'var(--text-subtle)'
      }
    }, r.id))), /*#__PURE__*/React.createElement("td", {
      style: {
        ...TD,
        color: 'var(--text-muted)'
      }
    }, r.owner), /*#__PURE__*/React.createElement("td", {
      style: TD
    }, /*#__PURE__*/React.createElement("div", {
      style: {
        display: 'flex',
        alignItems: 'center',
        gap: 8
      }
    }, /*#__PURE__*/React.createElement(Badge, {
      tone: TONE[r.status],
      dot: true
    }, r.status), r.status === 'Running' ? /*#__PURE__*/React.createElement(ProgressRing, {
      value: r.pct,
      size: 22
    }) : null)), /*#__PURE__*/React.createElement("td", {
      style: {
        ...TD,
        fontVariantNumeric: 'tabular-nums',
        color: 'var(--text-muted)'
      }
    }, r.dur), /*#__PURE__*/React.createElement("td", {
      style: {
        ...TD,
        color: 'var(--text-muted)'
      }
    }, r.when), /*#__PURE__*/React.createElement("td", {
      style: {
        ...TD,
        textAlign: 'right'
      }
    }, /*#__PURE__*/React.createElement("div", {
      style: {
        display: 'flex',
        gap: 2,
        justifyContent: 'flex-end'
      }
    }, /*#__PURE__*/React.createElement(Tooltip, {
      label: "Open run"
    }, /*#__PURE__*/React.createElement(IconButton, {
      icon: "arrow-up-right",
      label: "Open run",
      size: "sm",
      onClick: () => onOpen(r)
    })), /*#__PURE__*/React.createElement(Tooltip, {
      label: "Retry",
      placement: "left"
    }, /*#__PURE__*/React.createElement(IconButton, {
      icon: "rotate-ccw",
      label: "Retry",
      size: "sm"
    })))));
  }), rows.length === 0 ? /*#__PURE__*/React.createElement("tr", null, /*#__PURE__*/React.createElement("td", {
    colSpan: 7,
    style: {
      ...TD,
      textAlign: 'center',
      padding: 'var(--space-11)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 6,
      justifyItems: 'center'
    }
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "search-x",
    size: 26,
    color: "var(--text-subtle)"
  }), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-subhead)',
      color: 'var(--text-heading)',
      fontWeight: 600
    }
  }, "No runs match"), /*#__PURE__*/React.createElement("span", {
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-muted)'
    }
  }, "Clear the filter or widen the status.")))) : null)));
}
Object.assign(window, {
  RunsTable,
  ROWS
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/internal_tool/RunsTable.jsx", error: String((e && e.message) || e) }); }

// ui_kits/internal_tool/Shell.jsx
try { (() => {
const {
  Icon,
  IconButton,
  Button,
  Badge,
  Input,
  SegmentedControl
} = window.PersonalUIDesignSystem_06a1d8;
const RAIL = [{
  id: 'runs',
  label: 'Runs',
  icon: 'list-checks'
}, {
  id: 'jobs',
  label: 'Jobs',
  icon: 'server-cog'
}, {
  id: 'keys',
  label: 'API keys',
  icon: 'key-round'
}, {
  id: 'audit',
  label: 'Audit',
  icon: 'scroll-text'
}];
function Rail({
  view,
  onView
}) {
  return /*#__PURE__*/React.createElement("aside", {
    style: {
      width: 72,
      flex: '0 0 auto',
      padding: 'var(--space-5) 0',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      gap: 'var(--space-5)',
      borderRight: '1px solid var(--border-subtle)',
      background: 'var(--surface-card)'
    }
  }, /*#__PURE__*/React.createElement("span", {
    style: {
      width: 26,
      height: 26,
      borderRadius: 999,
      background: 'var(--accent)',
      boxShadow: 'var(--shadow-accent)'
    }
  }), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 4
    }
  }, RAIL.map(r => /*#__PURE__*/React.createElement(IconButton, {
    key: r.id,
    icon: r.icon,
    label: r.label,
    active: r.id === view,
    onClick: () => onView(r.id)
  }))), /*#__PURE__*/React.createElement("div", {
    style: {
      marginTop: 'auto',
      display: 'grid',
      gap: 4,
      justifyItems: 'center'
    }
  }, /*#__PURE__*/React.createElement(IconButton, {
    icon: "settings",
    label: "Settings"
  }), /*#__PURE__*/React.createElement("span", {
    style: {
      width: 30,
      height: 30,
      borderRadius: 999,
      background: 'var(--info-soft)',
      color: 'var(--info-on-soft)',
      display: 'grid',
      placeItems: 'center',
      font: 'var(--weight-bold) 12px/1 var(--font-core)'
    }
  }, "R")));
}
function Toolbar({
  title,
  count,
  query,
  onQuery,
  status,
  onStatus,
  onNew
}) {
  return /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'grid',
      gap: 'var(--space-6)',
      paddingBottom: 'var(--space-6)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      gap: 'var(--space-6)'
    }
  }, /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'baseline',
      gap: 10
    }
  }, /*#__PURE__*/React.createElement("h2", {
    style: {
      font: 'var(--type-title-1)',
      letterSpacing: 'var(--tracking-title)',
      color: 'var(--text-heading)'
    }
  }, title), /*#__PURE__*/React.createElement("span", {
    className: "tabular",
    style: {
      font: 'var(--type-footnote)',
      color: 'var(--text-subtle)'
    }
  }, count, " records")), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      gap: 'var(--gap-inline)'
    }
  }, /*#__PURE__*/React.createElement(Button, {
    variant: "secondary",
    icon: "download"
  }, "Export"), /*#__PURE__*/React.createElement(Button, {
    icon: "play",
    onClick: onNew
  }, "New run"))), /*#__PURE__*/React.createElement("div", {
    style: {
      display: 'flex',
      alignItems: 'center',
      gap: 'var(--gap-inline)',
      flexWrap: 'wrap'
    }
  }, /*#__PURE__*/React.createElement(Input, {
    icon: "search",
    placeholder: "Filter by id, owner or target",
    value: query,
    onChange: e => onQuery(e.target.value),
    size: "sm",
    style: {
      width: 300
    }
  }), /*#__PURE__*/React.createElement(SegmentedControl, {
    size: "sm",
    value: status,
    onChange: onStatus,
    items: ['All', 'Running', 'Failed']
  })));
}
Object.assign(window, {
  Rail,
  Toolbar,
  RAIL
});
})(); } catch (e) { __ds_ns.__errors.push({ path: "ui_kits/internal_tool/Shell.jsx", error: String((e && e.message) || e) }); }

__ds_ns.Badge = __ds_scope.Badge;

__ds_ns.Button = __ds_scope.Button;

__ds_ns.Card = __ds_scope.Card;

__ds_ns.CardHeader = __ds_scope.CardHeader;

__ds_ns.Icon = __ds_scope.Icon;

__ds_ns.IconButton = __ds_scope.IconButton;

__ds_ns.Tag = __ds_scope.Tag;

__ds_ns.ProgressRing = __ds_scope.ProgressRing;

__ds_ns.Stat = __ds_scope.Stat;

__ds_ns.Dialog = __ds_scope.Dialog;

__ds_ns.Toast = __ds_scope.Toast;

__ds_ns.Tooltip = __ds_scope.Tooltip;

__ds_ns.Checkbox = __ds_scope.Checkbox;

__ds_ns.Field = __ds_scope.Field;

__ds_ns.Input = __ds_scope.Input;

__ds_ns.Radio = __ds_scope.Radio;

__ds_ns.Select = __ds_scope.Select;

__ds_ns.Slider = __ds_scope.Slider;

__ds_ns.Switch = __ds_scope.Switch;

__ds_ns.ListRow = __ds_scope.ListRow;

__ds_ns.SegmentedControl = __ds_scope.SegmentedControl;

__ds_ns.Tabs = __ds_scope.Tabs;

})();
