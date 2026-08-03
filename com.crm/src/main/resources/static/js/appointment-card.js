// node_modules/@lit/reactive-element/css-tag.js
var t = globalThis;
var e = t.ShadowRoot && (void 0 === t.ShadyCSS || t.ShadyCSS.nativeShadow) && "adoptedStyleSheets" in Document.prototype && "replace" in CSSStyleSheet.prototype;
var s = /* @__PURE__ */ Symbol();
var o = /* @__PURE__ */ new WeakMap();
var n = class {
  constructor(t3, e4, o5) {
    if (this._$cssResult$ = true, o5 !== s) throw Error("CSSResult is not constructable. Use `unsafeCSS` or `css` instead.");
    this.cssText = t3, this.t = e4;
  }
  get styleSheet() {
    let t3 = this.o;
    const s4 = this.t;
    if (e && void 0 === t3) {
      const e4 = void 0 !== s4 && 1 === s4.length;
      e4 && (t3 = o.get(s4)), void 0 === t3 && ((this.o = t3 = new CSSStyleSheet()).replaceSync(this.cssText), e4 && o.set(s4, t3));
    }
    return t3;
  }
  toString() {
    return this.cssText;
  }
};
var r = (t3) => new n("string" == typeof t3 ? t3 : t3 + "", void 0, s);
var i = (t3, ...e4) => {
  const o5 = 1 === t3.length ? t3[0] : e4.reduce((e5, s4, o6) => e5 + ((t4) => {
    if (true === t4._$cssResult$) return t4.cssText;
    if ("number" == typeof t4) return t4;
    throw Error("Value passed to 'css' function must be a 'css' function result: " + t4 + ". Use 'unsafeCSS' to pass non-literal values, but take care to ensure page security.");
  })(s4) + t3[o6 + 1], t3[0]);
  return new n(o5, t3, s);
};
var S = (s4, o5) => {
  if (e) s4.adoptedStyleSheets = o5.map((t3) => t3 instanceof CSSStyleSheet ? t3 : t3.styleSheet);
  else for (const e4 of o5) {
    const o6 = document.createElement("style"), n4 = t.litNonce;
    void 0 !== n4 && o6.setAttribute("nonce", n4), o6.textContent = e4.cssText, s4.appendChild(o6);
  }
};
var c = e ? (t3) => t3 : (t3) => t3 instanceof CSSStyleSheet ? ((t4) => {
  let e4 = "";
  for (const s4 of t4.cssRules) e4 += s4.cssText;
  return r(e4);
})(t3) : t3;

// node_modules/@lit/reactive-element/reactive-element.js
var { is: i2, defineProperty: e2, getOwnPropertyDescriptor: h, getOwnPropertyNames: r2, getOwnPropertySymbols: o2, getPrototypeOf: n2 } = Object;
var a = globalThis;
var c2 = a.trustedTypes;
var l = c2 ? c2.emptyScript : "";
var p = a.reactiveElementPolyfillSupport;
var d = (t3, s4) => t3;
var u = { toAttribute(t3, s4) {
  switch (s4) {
    case Boolean:
      t3 = t3 ? l : null;
      break;
    case Object:
    case Array:
      t3 = null == t3 ? t3 : JSON.stringify(t3);
  }
  return t3;
}, fromAttribute(t3, s4) {
  let i5 = t3;
  switch (s4) {
    case Boolean:
      i5 = null !== t3;
      break;
    case Number:
      i5 = null === t3 ? null : Number(t3);
      break;
    case Object:
    case Array:
      try {
        i5 = JSON.parse(t3);
      } catch (t4) {
        i5 = null;
      }
  }
  return i5;
} };
var f = (t3, s4) => !i2(t3, s4);
var b = { attribute: true, type: String, converter: u, reflect: false, useDefault: false, hasChanged: f };
Symbol.metadata ??= /* @__PURE__ */ Symbol("metadata"), a.litPropertyMetadata ??= /* @__PURE__ */ new WeakMap();
var y = class extends HTMLElement {
  static addInitializer(t3) {
    this._$Ei(), (this.l ??= []).push(t3);
  }
  static get observedAttributes() {
    return this.finalize(), this._$Eh && [...this._$Eh.keys()];
  }
  static createProperty(t3, s4 = b) {
    if (s4.state && (s4.attribute = false), this._$Ei(), this.prototype.hasOwnProperty(t3) && ((s4 = Object.create(s4)).wrapped = true), this.elementProperties.set(t3, s4), !s4.noAccessor) {
      const i5 = /* @__PURE__ */ Symbol(), h3 = this.getPropertyDescriptor(t3, i5, s4);
      void 0 !== h3 && e2(this.prototype, t3, h3);
    }
  }
  static getPropertyDescriptor(t3, s4, i5) {
    const { get: e4, set: r4 } = h(this.prototype, t3) ?? { get() {
      return this[s4];
    }, set(t4) {
      this[s4] = t4;
    } };
    return { get: e4, set(s5) {
      const h3 = e4?.call(this);
      r4?.call(this, s5), this.requestUpdate(t3, h3, i5);
    }, configurable: true, enumerable: true };
  }
  static getPropertyOptions(t3) {
    return this.elementProperties.get(t3) ?? b;
  }
  static _$Ei() {
    if (this.hasOwnProperty(d("elementProperties"))) return;
    const t3 = n2(this);
    t3.finalize(), void 0 !== t3.l && (this.l = [...t3.l]), this.elementProperties = new Map(t3.elementProperties);
  }
  static finalize() {
    if (this.hasOwnProperty(d("finalized"))) return;
    if (this.finalized = true, this._$Ei(), this.hasOwnProperty(d("properties"))) {
      const t4 = this.properties, s4 = [...r2(t4), ...o2(t4)];
      for (const i5 of s4) this.createProperty(i5, t4[i5]);
    }
    const t3 = this[Symbol.metadata];
    if (null !== t3) {
      const s4 = litPropertyMetadata.get(t3);
      if (void 0 !== s4) for (const [t4, i5] of s4) this.elementProperties.set(t4, i5);
    }
    this._$Eh = /* @__PURE__ */ new Map();
    for (const [t4, s4] of this.elementProperties) {
      const i5 = this._$Eu(t4, s4);
      void 0 !== i5 && this._$Eh.set(i5, t4);
    }
    this.elementStyles = this.finalizeStyles(this.styles);
  }
  static finalizeStyles(s4) {
    const i5 = [];
    if (Array.isArray(s4)) {
      const e4 = new Set(s4.flat(1 / 0).reverse());
      for (const s5 of e4) i5.unshift(c(s5));
    } else void 0 !== s4 && i5.push(c(s4));
    return i5;
  }
  static _$Eu(t3, s4) {
    const i5 = s4.attribute;
    return false === i5 ? void 0 : "string" == typeof i5 ? i5 : "string" == typeof t3 ? t3.toLowerCase() : void 0;
  }
  constructor() {
    super(), this._$Ep = void 0, this.isUpdatePending = false, this.hasUpdated = false, this._$Em = null, this._$Ev();
  }
  _$Ev() {
    this._$ES = new Promise((t3) => this.enableUpdating = t3), this._$AL = /* @__PURE__ */ new Map(), this._$E_(), this.requestUpdate(), this.constructor.l?.forEach((t3) => t3(this));
  }
  addController(t3) {
    (this._$EO ??= /* @__PURE__ */ new Set()).add(t3), void 0 !== this.renderRoot && this.isConnected && t3.hostConnected?.();
  }
  removeController(t3) {
    this._$EO?.delete(t3);
  }
  _$E_() {
    const t3 = /* @__PURE__ */ new Map(), s4 = this.constructor.elementProperties;
    for (const i5 of s4.keys()) this.hasOwnProperty(i5) && (t3.set(i5, this[i5]), delete this[i5]);
    t3.size > 0 && (this._$Ep = t3);
  }
  createRenderRoot() {
    const t3 = this.shadowRoot ?? this.attachShadow(this.constructor.shadowRootOptions);
    return S(t3, this.constructor.elementStyles), t3;
  }
  connectedCallback() {
    this.renderRoot ??= this.createRenderRoot(), this.enableUpdating(true), this._$EO?.forEach((t3) => t3.hostConnected?.());
  }
  enableUpdating(t3) {
  }
  disconnectedCallback() {
    this._$EO?.forEach((t3) => t3.hostDisconnected?.());
  }
  attributeChangedCallback(t3, s4, i5) {
    this._$AK(t3, i5);
  }
  _$ET(t3, s4) {
    const i5 = this.constructor.elementProperties.get(t3), e4 = this.constructor._$Eu(t3, i5);
    if (void 0 !== e4 && true === i5.reflect) {
      const h3 = (void 0 !== i5.converter?.toAttribute ? i5.converter : u).toAttribute(s4, i5.type);
      this._$Em = t3, null == h3 ? this.removeAttribute(e4) : this.setAttribute(e4, h3), this._$Em = null;
    }
  }
  _$AK(t3, s4) {
    const i5 = this.constructor, e4 = i5._$Eh.get(t3);
    if (void 0 !== e4 && this._$Em !== e4) {
      const t4 = i5.getPropertyOptions(e4), h3 = "function" == typeof t4.converter ? { fromAttribute: t4.converter } : void 0 !== t4.converter?.fromAttribute ? t4.converter : u;
      this._$Em = e4;
      const r4 = h3.fromAttribute(s4, t4.type);
      this[e4] = r4 ?? this._$Ej?.get(e4) ?? r4, this._$Em = null;
    }
  }
  requestUpdate(t3, s4, i5, e4 = false, h3) {
    if (void 0 !== t3) {
      const r4 = this.constructor;
      if (false === e4 && (h3 = this[t3]), i5 ??= r4.getPropertyOptions(t3), !((i5.hasChanged ?? f)(h3, s4) || i5.useDefault && i5.reflect && h3 === this._$Ej?.get(t3) && !this.hasAttribute(r4._$Eu(t3, i5)))) return;
      this.C(t3, s4, i5);
    }
    false === this.isUpdatePending && (this._$ES = this._$EP());
  }
  C(t3, s4, { useDefault: i5, reflect: e4, wrapped: h3 }, r4) {
    i5 && !(this._$Ej ??= /* @__PURE__ */ new Map()).has(t3) && (this._$Ej.set(t3, r4 ?? s4 ?? this[t3]), true !== h3 || void 0 !== r4) || (this._$AL.has(t3) || (this.hasUpdated || i5 || (s4 = void 0), this._$AL.set(t3, s4)), true === e4 && this._$Em !== t3 && (this._$Eq ??= /* @__PURE__ */ new Set()).add(t3));
  }
  async _$EP() {
    this.isUpdatePending = true;
    try {
      await this._$ES;
    } catch (t4) {
      Promise.reject(t4);
    }
    const t3 = this.scheduleUpdate();
    return null != t3 && await t3, !this.isUpdatePending;
  }
  scheduleUpdate() {
    return this.performUpdate();
  }
  performUpdate() {
    if (!this.isUpdatePending) return;
    if (!this.hasUpdated) {
      if (this.renderRoot ??= this.createRenderRoot(), this._$Ep) {
        for (const [t5, s5] of this._$Ep) this[t5] = s5;
        this._$Ep = void 0;
      }
      const t4 = this.constructor.elementProperties;
      if (t4.size > 0) for (const [s5, i5] of t4) {
        const { wrapped: t5 } = i5, e4 = this[s5];
        true !== t5 || this._$AL.has(s5) || void 0 === e4 || this.C(s5, void 0, i5, e4);
      }
    }
    let t3 = false;
    const s4 = this._$AL;
    try {
      t3 = this.shouldUpdate(s4), t3 ? (this.willUpdate(s4), this._$EO?.forEach((t4) => t4.hostUpdate?.()), this.update(s4)) : this._$EM();
    } catch (s5) {
      throw t3 = false, this._$EM(), s5;
    }
    t3 && this._$AE(s4);
  }
  willUpdate(t3) {
  }
  _$AE(t3) {
    this._$EO?.forEach((t4) => t4.hostUpdated?.()), this.hasUpdated || (this.hasUpdated = true, this.firstUpdated(t3)), this.updated(t3);
  }
  _$EM() {
    this._$AL = /* @__PURE__ */ new Map(), this.isUpdatePending = false;
  }
  get updateComplete() {
    return this.getUpdateComplete();
  }
  getUpdateComplete() {
    return this._$ES;
  }
  shouldUpdate(t3) {
    return true;
  }
  update(t3) {
    this._$Eq &&= this._$Eq.forEach((t4) => this._$ET(t4, this[t4])), this._$EM();
  }
  updated(t3) {
  }
  firstUpdated(t3) {
  }
};
y.elementStyles = [], y.shadowRootOptions = { mode: "open" }, y[d("elementProperties")] = /* @__PURE__ */ new Map(), y[d("finalized")] = /* @__PURE__ */ new Map(), p?.({ ReactiveElement: y }), (a.reactiveElementVersions ??= []).push("2.1.2");

// node_modules/lit-html/lit-html.js
var t2 = globalThis;
var i3 = (t3) => t3;
var s2 = t2.trustedTypes;
var e3 = s2 ? s2.createPolicy("lit-html", { createHTML: (t3) => t3 }) : void 0;
var h2 = "$lit$";
var o3 = `lit$${Math.random().toFixed(9).slice(2)}$`;
var n3 = "?" + o3;
var r3 = `<${n3}>`;
var l2 = document;
var c3 = () => l2.createComment("");
var a2 = (t3) => null === t3 || "object" != typeof t3 && "function" != typeof t3;
var u2 = Array.isArray;
var d2 = (t3) => u2(t3) || "function" == typeof t3?.[Symbol.iterator];
var f2 = "[ 	\n\f\r]";
var v = /<(?:(!--|\/[^a-zA-Z])|(\/?[a-zA-Z][^>\s]*)|(\/?$))/g;
var _ = /-->/g;
var m = />/g;
var p2 = RegExp(`>|${f2}(?:([^\\s"'>=/]+)(${f2}*=${f2}*(?:[^ 	
\f\r"'\`<>=]|("|')|))|$)`, "g");
var g = /'/g;
var $ = /"/g;
var y2 = /^(?:script|style|textarea|title)$/i;
var x = (t3) => (i5, ...s4) => ({ _$litType$: t3, strings: i5, values: s4 });
var b2 = x(1);
var w = x(2);
var T = x(3);
var E = /* @__PURE__ */ Symbol.for("lit-noChange");
var A = /* @__PURE__ */ Symbol.for("lit-nothing");
var C = /* @__PURE__ */ new WeakMap();
var P = l2.createTreeWalker(l2, 129);
function V(t3, i5) {
  if (!u2(t3) || !t3.hasOwnProperty("raw")) throw Error("invalid template strings array");
  return void 0 !== e3 ? e3.createHTML(i5) : i5;
}
var N = (t3, i5) => {
  const s4 = t3.length - 1, e4 = [];
  let n4, l3 = 2 === i5 ? "<svg>" : 3 === i5 ? "<math>" : "", c4 = v;
  for (let i6 = 0; i6 < s4; i6++) {
    const s5 = t3[i6];
    let a3, u3, d3 = -1, f3 = 0;
    for (; f3 < s5.length && (c4.lastIndex = f3, u3 = c4.exec(s5), null !== u3); ) f3 = c4.lastIndex, c4 === v ? "!--" === u3[1] ? c4 = _ : void 0 !== u3[1] ? c4 = m : void 0 !== u3[2] ? (y2.test(u3[2]) && (n4 = RegExp("</" + u3[2], "g")), c4 = p2) : void 0 !== u3[3] && (c4 = p2) : c4 === p2 ? ">" === u3[0] ? (c4 = n4 ?? v, d3 = -1) : void 0 === u3[1] ? d3 = -2 : (d3 = c4.lastIndex - u3[2].length, a3 = u3[1], c4 = void 0 === u3[3] ? p2 : '"' === u3[3] ? $ : g) : c4 === $ || c4 === g ? c4 = p2 : c4 === _ || c4 === m ? c4 = v : (c4 = p2, n4 = void 0);
    const x2 = c4 === p2 && t3[i6 + 1].startsWith("/>") ? " " : "";
    l3 += c4 === v ? s5 + r3 : d3 >= 0 ? (e4.push(a3), s5.slice(0, d3) + h2 + s5.slice(d3) + o3 + x2) : s5 + o3 + (-2 === d3 ? i6 : x2);
  }
  return [V(t3, l3 + (t3[s4] || "<?>") + (2 === i5 ? "</svg>" : 3 === i5 ? "</math>" : "")), e4];
};
var S2 = class _S {
  constructor({ strings: t3, _$litType$: i5 }, e4) {
    let r4;
    this.parts = [];
    let l3 = 0, a3 = 0;
    const u3 = t3.length - 1, d3 = this.parts, [f3, v2] = N(t3, i5);
    if (this.el = _S.createElement(f3, e4), P.currentNode = this.el.content, 2 === i5 || 3 === i5) {
      const t4 = this.el.content.firstChild;
      t4.replaceWith(...t4.childNodes);
    }
    for (; null !== (r4 = P.nextNode()) && d3.length < u3; ) {
      if (1 === r4.nodeType) {
        if (r4.hasAttributes()) for (const t4 of r4.getAttributeNames()) if (t4.endsWith(h2)) {
          const i6 = v2[a3++], s4 = r4.getAttribute(t4).split(o3), e5 = /([.?@])?(.*)/.exec(i6);
          d3.push({ type: 1, index: l3, name: e5[2], strings: s4, ctor: "." === e5[1] ? I : "?" === e5[1] ? L : "@" === e5[1] ? z : H }), r4.removeAttribute(t4);
        } else t4.startsWith(o3) && (d3.push({ type: 6, index: l3 }), r4.removeAttribute(t4));
        if (y2.test(r4.tagName)) {
          const t4 = r4.textContent.split(o3), i6 = t4.length - 1;
          if (i6 > 0) {
            r4.textContent = s2 ? s2.emptyScript : "";
            for (let s4 = 0; s4 < i6; s4++) r4.append(t4[s4], c3()), P.nextNode(), d3.push({ type: 2, index: ++l3 });
            r4.append(t4[i6], c3());
          }
        }
      } else if (8 === r4.nodeType) if (r4.data === n3) d3.push({ type: 2, index: l3 });
      else {
        let t4 = -1;
        for (; -1 !== (t4 = r4.data.indexOf(o3, t4 + 1)); ) d3.push({ type: 7, index: l3 }), t4 += o3.length - 1;
      }
      l3++;
    }
  }
  static createElement(t3, i5) {
    const s4 = l2.createElement("template");
    return s4.innerHTML = t3, s4;
  }
};
function M(t3, i5, s4 = t3, e4) {
  if (i5 === E) return i5;
  let h3 = void 0 !== e4 ? s4._$Co?.[e4] : s4._$Cl;
  const o5 = a2(i5) ? void 0 : i5._$litDirective$;
  return h3?.constructor !== o5 && (h3?._$AO?.(false), void 0 === o5 ? h3 = void 0 : (h3 = new o5(t3), h3._$AT(t3, s4, e4)), void 0 !== e4 ? (s4._$Co ??= [])[e4] = h3 : s4._$Cl = h3), void 0 !== h3 && (i5 = M(t3, h3._$AS(t3, i5.values), h3, e4)), i5;
}
var R = class {
  constructor(t3, i5) {
    this._$AV = [], this._$AN = void 0, this._$AD = t3, this._$AM = i5;
  }
  get parentNode() {
    return this._$AM.parentNode;
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  u(t3) {
    const { el: { content: i5 }, parts: s4 } = this._$AD, e4 = (t3?.creationScope ?? l2).importNode(i5, true);
    P.currentNode = e4;
    let h3 = P.nextNode(), o5 = 0, n4 = 0, r4 = s4[0];
    for (; void 0 !== r4; ) {
      if (o5 === r4.index) {
        let i6;
        2 === r4.type ? i6 = new k(h3, h3.nextSibling, this, t3) : 1 === r4.type ? i6 = new r4.ctor(h3, r4.name, r4.strings, this, t3) : 6 === r4.type && (i6 = new Z(h3, this, t3)), this._$AV.push(i6), r4 = s4[++n4];
      }
      o5 !== r4?.index && (h3 = P.nextNode(), o5++);
    }
    return P.currentNode = l2, e4;
  }
  p(t3) {
    let i5 = 0;
    for (const s4 of this._$AV) void 0 !== s4 && (void 0 !== s4.strings ? (s4._$AI(t3, s4, i5), i5 += s4.strings.length - 2) : s4._$AI(t3[i5])), i5++;
  }
};
var k = class _k {
  get _$AU() {
    return this._$AM?._$AU ?? this._$Cv;
  }
  constructor(t3, i5, s4, e4) {
    this.type = 2, this._$AH = A, this._$AN = void 0, this._$AA = t3, this._$AB = i5, this._$AM = s4, this.options = e4, this._$Cv = e4?.isConnected ?? true;
  }
  get parentNode() {
    let t3 = this._$AA.parentNode;
    const i5 = this._$AM;
    return void 0 !== i5 && 11 === t3?.nodeType && (t3 = i5.parentNode), t3;
  }
  get startNode() {
    return this._$AA;
  }
  get endNode() {
    return this._$AB;
  }
  _$AI(t3, i5 = this) {
    t3 = M(this, t3, i5), a2(t3) ? t3 === A || null == t3 || "" === t3 ? (this._$AH !== A && this._$AR(), this._$AH = A) : t3 !== this._$AH && t3 !== E && this._(t3) : void 0 !== t3._$litType$ ? this.$(t3) : void 0 !== t3.nodeType ? this.T(t3) : d2(t3) ? this.k(t3) : this._(t3);
  }
  O(t3) {
    return this._$AA.parentNode.insertBefore(t3, this._$AB);
  }
  T(t3) {
    this._$AH !== t3 && (this._$AR(), this._$AH = this.O(t3));
  }
  _(t3) {
    this._$AH !== A && a2(this._$AH) ? this._$AA.nextSibling.data = t3 : this.T(l2.createTextNode(t3)), this._$AH = t3;
  }
  $(t3) {
    const { values: i5, _$litType$: s4 } = t3, e4 = "number" == typeof s4 ? this._$AC(t3) : (void 0 === s4.el && (s4.el = S2.createElement(V(s4.h, s4.h[0]), this.options)), s4);
    if (this._$AH?._$AD === e4) this._$AH.p(i5);
    else {
      const t4 = new R(e4, this), s5 = t4.u(this.options);
      t4.p(i5), this.T(s5), this._$AH = t4;
    }
  }
  _$AC(t3) {
    let i5 = C.get(t3.strings);
    return void 0 === i5 && C.set(t3.strings, i5 = new S2(t3)), i5;
  }
  k(t3) {
    u2(this._$AH) || (this._$AH = [], this._$AR());
    const i5 = this._$AH;
    let s4, e4 = 0;
    for (const h3 of t3) e4 === i5.length ? i5.push(s4 = new _k(this.O(c3()), this.O(c3()), this, this.options)) : s4 = i5[e4], s4._$AI(h3), e4++;
    e4 < i5.length && (this._$AR(s4 && s4._$AB.nextSibling, e4), i5.length = e4);
  }
  _$AR(t3 = this._$AA.nextSibling, s4) {
    for (this._$AP?.(false, true, s4); t3 !== this._$AB; ) {
      const s5 = i3(t3).nextSibling;
      i3(t3).remove(), t3 = s5;
    }
  }
  setConnected(t3) {
    void 0 === this._$AM && (this._$Cv = t3, this._$AP?.(t3));
  }
};
var H = class {
  get tagName() {
    return this.element.tagName;
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  constructor(t3, i5, s4, e4, h3) {
    this.type = 1, this._$AH = A, this._$AN = void 0, this.element = t3, this.name = i5, this._$AM = e4, this.options = h3, s4.length > 2 || "" !== s4[0] || "" !== s4[1] ? (this._$AH = Array(s4.length - 1).fill(new String()), this.strings = s4) : this._$AH = A;
  }
  _$AI(t3, i5 = this, s4, e4) {
    const h3 = this.strings;
    let o5 = false;
    if (void 0 === h3) t3 = M(this, t3, i5, 0), o5 = !a2(t3) || t3 !== this._$AH && t3 !== E, o5 && (this._$AH = t3);
    else {
      const e5 = t3;
      let n4, r4;
      for (t3 = h3[0], n4 = 0; n4 < h3.length - 1; n4++) r4 = M(this, e5[s4 + n4], i5, n4), r4 === E && (r4 = this._$AH[n4]), o5 ||= !a2(r4) || r4 !== this._$AH[n4], r4 === A ? t3 = A : t3 !== A && (t3 += (r4 ?? "") + h3[n4 + 1]), this._$AH[n4] = r4;
    }
    o5 && !e4 && this.j(t3);
  }
  j(t3) {
    t3 === A ? this.element.removeAttribute(this.name) : this.element.setAttribute(this.name, t3 ?? "");
  }
};
var I = class extends H {
  constructor() {
    super(...arguments), this.type = 3;
  }
  j(t3) {
    this.element[this.name] = t3 === A ? void 0 : t3;
  }
};
var L = class extends H {
  constructor() {
    super(...arguments), this.type = 4;
  }
  j(t3) {
    this.element.toggleAttribute(this.name, !!t3 && t3 !== A);
  }
};
var z = class extends H {
  constructor(t3, i5, s4, e4, h3) {
    super(t3, i5, s4, e4, h3), this.type = 5;
  }
  _$AI(t3, i5 = this) {
    if ((t3 = M(this, t3, i5, 0) ?? A) === E) return;
    const s4 = this._$AH, e4 = t3 === A && s4 !== A || t3.capture !== s4.capture || t3.once !== s4.once || t3.passive !== s4.passive, h3 = t3 !== A && (s4 === A || e4);
    e4 && this.element.removeEventListener(this.name, this, s4), h3 && this.element.addEventListener(this.name, this, t3), this._$AH = t3;
  }
  handleEvent(t3) {
    "function" == typeof this._$AH ? this._$AH.call(this.options?.host ?? this.element, t3) : this._$AH.handleEvent(t3);
  }
};
var Z = class {
  constructor(t3, i5, s4) {
    this.element = t3, this.type = 6, this._$AN = void 0, this._$AM = i5, this.options = s4;
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  _$AI(t3) {
    M(this, t3);
  }
};
var B = t2.litHtmlPolyfillSupport;
B?.(S2, k), (t2.litHtmlVersions ??= []).push("3.3.3");
var D = (t3, i5, s4) => {
  const e4 = s4?.renderBefore ?? i5;
  let h3 = e4._$litPart$;
  if (void 0 === h3) {
    const t4 = s4?.renderBefore ?? null;
    e4._$litPart$ = h3 = new k(i5.insertBefore(c3(), t4), t4, void 0, s4 ?? {});
  }
  return h3._$AI(t3), h3;
};

// node_modules/lit-element/lit-element.js
var s3 = globalThis;
var i4 = class extends y {
  constructor() {
    super(...arguments), this.renderOptions = { host: this }, this._$Do = void 0;
  }
  createRenderRoot() {
    const t3 = super.createRenderRoot();
    return this.renderOptions.renderBefore ??= t3.firstChild, t3;
  }
  update(t3) {
    const r4 = this.render();
    this.hasUpdated || (this.renderOptions.isConnected = this.isConnected), super.update(t3), this._$Do = D(r4, this.renderRoot, this.renderOptions);
  }
  connectedCallback() {
    super.connectedCallback(), this._$Do?.setConnected(true);
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this._$Do?.setConnected(false);
  }
  render() {
    return E;
  }
};
i4._$litElement$ = true, i4["finalized"] = true, s3.litElementHydrateSupport?.({ LitElement: i4 });
var o4 = s3.litElementPolyfillSupport;
o4?.({ LitElement: i4 });
(s3.litElementVersions ??= []).push("4.2.2");

// src/appointment-card.js
var AppointmentCard = class extends i4 {
  static styles = i`
    :host {
      --font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
      --primary-color: #3b82f6;
      --primary-hover: #2563eb;
      --danger-color: #ef4444;
      --danger-hover: #dc2626;
      --secondary-color: #334155;
      --secondary-hover: #1e293b;
      
      --bg-surface: #1e293b;
      --bg-main: #0f172a;
      --text-main: #f8fafc;
      --text-muted: #94a3b8;
      --text-light: #ffffff;
      --border-color: #334155;
      
      --radius-sm: 6px;
      --radius-md: 12px;
      --shadow-sm: 0 4px 6px -1px rgba(0, 0, 0, 0.3), 0 2px 4px -1px rgba(0, 0, 0, 0.18);
      --shadow-lg: 0 20px 25px -5px rgba(0, 0, 0, 0.5), 0 10px 10px -5px rgba(0, 0, 0, 0.3);
    }

    .appointmentContainer p {
      margin: 0 0 12px 0;
      color: var(--text-main);
      font-size: 0.95rem;
      line-height: 1.5;
    }   
    
    .appointmentContainer {
      background-color: var(--bg-surface);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-md);
      padding: 20px;
      display: flex;
      flex-direction: column;
      align-items: stretch;
      box-shadow: var(--shadow-sm);
      transition: box-shadow 0.2s ease, transform 0.2s ease;
    }

    .appointmentContainer:hover {
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.4), 0 4px 6px -2px rgba(0, 0, 0, 0.2);
    }

    .status-badge {
      display: inline-block;
      padding: 4px 10px;
      font-size: 0.75rem;
      font-weight: 600;
      border-radius: var(--radius-sm);
      text-transform: uppercase;
      letter-spacing: 0.05em;
      margin-bottom: 12px;
      width: fit-content;
    }

    .status-badge.confirmed {
      background-color: rgba(34, 197, 94, 0.2);
      color: #4ade80;
      border: 1px solid rgba(34, 197, 94, 0.3);
    }

    .status-badge.cancelled {
      background-color: rgba(239, 68, 68, 0.2);
      color: #f87171;
      border: 1px solid rgba(239, 68, 68, 0.3);
    }

    .status-badge.pending {
      background-color: rgba(234, 179, 8, 0.2);
      color: #facc15;
      border: 1px solid rgba(234, 179, 8, 0.3);
    }

    .buttonContainer {
      display: flex;
      flex-direction: row; 
      justify-content: flex-end;
      gap: 12px;
      margin-top: 16px;
      padding-top: 12px;
      border-top: 1px solid var(--border-color);
    }

    .buttonContainer button,
    .buttonContainer .btn {
      font-family: inherit;
      font-size: 0.875rem;
      font-weight: 500;
      padding: 8px 16px;
      border-radius: var(--radius-sm);
      border: none;
      cursor: pointer;
      transition: background-color 0.2s ease, transform 0.1s ease;
    }

    .buttonContainer button:active,
    .buttonContainer .btn:active {
      transform: scale(0.98);
    }

    .buttonContainer .editAppointment,
    .buttonContainer .btn-primary {
      background-color: var(--primary-color);
      color: var(--text-light);
    }

    .buttonContainer .editAppointment:hover,
    .buttonContainer .btn-primary:hover {
      background-color: var(--primary-hover);
    }

    .buttonContainer .deleteAppointment,
    .buttonContainer .btn-danger {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .buttonContainer .deleteAppointment:hover,
    .buttonContainer .btn-danger:hover {
      background-color: var(--danger-hover);
    }

    .buttonContainer .cancelButton,
    .buttonContainer .btn-secondary {
      background-color: var(--secondary-color);
      color: var(--text-main);
      border: 1px solid var(--border-color);
    }

    .buttonContainer .cancelButton:hover,
    .buttonContainer .btn-secondary:hover {
      background-color: var(--secondary-hover);
      color: var(--text-light);
    }

    .buttonContainer .saveButton {
      background-color: #22c55e;
      color: var(--text-light);
    }

    .buttonContainer .saveButton:hover {
      background-color: #16a34a;
    }

    .buttonContainer .showTextLogs {
      background-color: #22c55e;
      color: var(--text-light);
    }

    .buttonContainer .showTextLogs:hover {
      background-color: #16a34a;
    }

    .editAppointmentPopup input,
    .editAppointmentPopup select,
    .createAppointmentPopup input,
    .createAppointmentPopup select {
      width: 100%;
      padding: 10px 12px;
      background-color: #0f172a;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      color: var(--text-main);
      font-size: 0.9rem;
      box-sizing: border-box;
      margin-top: 4px;
      margin-bottom: 8px;
    }

    .editAppointmentPopup input:focus,
    .editAppointmentPopup select:focus,
    .createAppointmentPopup input:focus,
    .createAppointmentPopup select:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
    }

    .editAppointmentPopup label,
    .createAppointmentPopup label {
      font-size: 0.85rem;
      color: var(--text-muted);
      font-weight: 500;
    }

    .editAppointmentPopup,
    .createAppointmentPopup,
    .textLogsPopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 32px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 600px;
      height: auto;
      max-height: 85vh;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      box-sizing: border-box;
      border: 1px solid var(--border-color);
      z-index: 1000;
      color: var(--text-main);
      overflow-y: auto;
    }
    
    .deleteAppointmentPopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 32px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 400px;
      height: auto;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      box-sizing: border-box;
      border: 1px solid var(--border-color);
      z-index: 1000;
      color: var(--text-main);
    }
    
    .deleteAppointmentPopup .deleteButton {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .removeVarBtn {
      background-color: var(--danger-color);
      color: var(--text-light);
      border: none;
      border-radius: var(--radius-sm);
      padding: 0 12px;
      cursor: pointer;
      font-weight: bold;
      height: 41px;
      margin-top: 4px;
      transition: background-color 0.2s ease;
    }

    .removeVarBtn:hover {
      background-color: var(--danger-hover);
    }

    .user-search-results {
      max-height: 150px;
      overflow-y: auto;
      background: var(--bg-main);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      margin-top: -4px;
      margin-bottom: 8px;
    }

    .user-search-item {
      padding: 8px 12px;
      cursor: pointer;
      font-size: 0.9rem;
      border-bottom: 1px solid var(--border-color);
    }

    .user-search-item:hover {
      background-color: var(--secondary-color);
    }

    .chat-container {
      display: flex;
      flex-direction: column;
      gap: 12px;
      max-height: 400px;
      overflow-y: auto;
      background-color: var(--bg-main);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      padding: 16px;
      margin-top: 16px;
      margin-bottom: 16px;
    }

    .chat-bubble {
      max-width: 75%;
      padding: 10px 14px;
      border-radius: var(--radius-md);
      font-size: 0.9rem;
      line-height: 1.4;
      word-break: break-word;
    }

    .chat-bubble.outbound {
      background-color: var(--primary-color);
      color: var(--text-light);
      align-self: flex-end;
      border-bottom-right-radius: 2px;
    }

    .chat-bubble.inbound {
      background-color: var(--secondary-color);
      color: var(--text-main);
      align-self: flex-start;
      border-bottom-left-radius: 2px;
      border: 1px solid var(--border-color);
    }

    .chat-timestamp {
      font-size: 0.7rem;
      color: var(--text-muted);
      margin-top: 4px;
      text-align: right;
    }
  `;
  static properties = {
    appointmentId: { type: Number },
    userId: { type: Number },
    name: { type: String },
    time: { type: String },
    confirmed: { type: Boolean },
    cancelled: { type: Boolean },
    vars: { type: Array },
    showEditPopup: { state: true },
    showDeletePopup: { state: true },
    showCreatePopup: { state: true },
    formUserId: { state: true },
    formUserName: { state: true },
    formTime: { state: true },
    newVarsArray: { state: true },
    userSearchResults: { state: true },
    showTextLogPopup: { state: true },
    smsLogs: { state: true }
  };
  constructor() {
    super();
    this.appointmentId = null;
    this.userId = null;
    this.name = "N/A";
    this.time = "N/A";
    this.confirmed = false;
    this.cancelled = false;
    this.vars = [];
    this.showEditPopup = false;
    this.showDeletePopup = false;
    this.showCreatePopup = false;
    this.showTextLogPopup = false;
    this.formUserId = null;
    this.formUserName = "";
    this.formTime = "";
    this.newVarsArray = [];
    this.userSearchResults = [];
    this.updateInterval = null;
    this.smsLogs = [];
  }
  connectedCallback() {
    super.connectedCallback();
    if (this.appointmentId != null && (!this.name || this.name === "N/A")) {
      this.updateAppointment();
    }
    if (this.appointmentId != null) {
      this.updateInterval = setInterval(() => {
        this.updateAppointment();
      }, 1e3);
    }
  }
  disconnectedCallback() {
    super.disconnectedCallback();
    if (this.updateInterval) {
      clearInterval(this.updateInterval);
    }
  }
  async updateAppointment() {
    if (this.appointmentId == null) {
      return;
    }
    try {
      let response = await fetch(`/api/appointments/get_appointment_by_id?id=${this.appointmentId}`);
      if (response.status === 404) {
        this.remove();
        return;
      }
      if (!response.ok) {
        throw new Error("Server error");
      }
      let appt = await response.json();
      this.userId = appt.userId ?? null;
      this.time = appt.time ?? "N/A";
      this.confirmed = Boolean(appt.confirmed);
      this.cancelled = Boolean(appt.cancelled);
      this.vars = appt.varList ?? [];
      if (this.userId != null) {
        let userResponse = await fetch(`/api/users/get_user_by_id?id=${this.userId}`);
        if (userResponse.ok) {
          let user = await userResponse.json();
          this.name = user.name ?? "N/A";
        } else {
          this.name = "N/A";
        }
      } else {
        this.name = "N/A";
      }
    } catch (error) {
      console.log(error);
    }
  }
  formatReadableTime(isoString) {
    if (!isoString || isoString === "N/A") return "N/A";
    try {
      const date = new Date(isoString);
      return date.toLocaleString(void 0, {
        dateStyle: "medium",
        timeStyle: "short"
      });
    } catch (e4) {
      return isoString;
    }
  }
  toLocalDatetimeInput(isoString) {
    if (!isoString) return "";
    try {
      const date = new Date(isoString);
      const tzOffset = date.getTimezoneOffset() * 6e4;
      const localISOTime = new Date(date.getTime() - tzOffset).toISOString().slice(0, 16);
      return localISOTime;
    } catch (e4) {
      return "";
    }
  }
  renderStatusIndicator() {
    let statusClass = "pending";
    let statusText = "Pending";
    if (this.cancelled) {
      statusClass = "cancelled";
      statusText = "Cancelled";
    } else if (this.confirmed) {
      statusClass = "confirmed";
      statusText = "Confirmed";
    }
    return b2`<div class="status-badge ${statusClass}">${statusText}</div>`;
  }
  varResolver() {
    if (this.vars.length === 0) {
      return b2`<p><br></p>`;
    }
    return b2`
      <p>Variables:</p>
      ${this.vars.map((variable) => b2`
        <p>Name: ${variable.varName}, Value: ${variable.varContents}</p>
      `)}
    `;
  }
  renderEditPopup() {
    if (!this.showEditPopup) return b2``;
    return b2`
      <div class="editAppointmentPopup">
        <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">Edit Appointment</p>
        
        <label>Search User (Last, First):</label>
        <input 
          type="text" 
          .value="${this.formUserName}" 
          @input="${this.handleUserSearchInput}" 
          placeholder="Type user name to search..."
        >
        ${this.userSearchResults.length > 0 ? b2`
          <div class="user-search-results">
            ${this.userSearchResults.map((user) => b2`
              <div class="user-search-item" @click="${() => this.selectUser(user)}">
                ${user.name} (ID: ${user.id})
              </div>
            `)}
          </div>
        ` : ""}

        <label>Selected User ID: ${this.formUserId ? this.formUserId : "None"}</label>

        <label style="margin-top: 12px;">Appointment Time:</label>
        <input 
          type="datetime-local" 
          .value="${this.formTime}" 
          @input="${(e4) => this.formTime = e4.target.value}"
        >

        ${this.editAppointmentPopupVars()}

        <button @click=${this.editAppointmentAddVariable} class="btn-secondary" style="margin-top: 8px;">
          Add Variable
        </button>

        <div class="buttonContainer">
          <button @click="${this.cancelEditAppointment}" class="cancelButton">Cancel</button>
          <button @click="${this.saveAppointment}" class="saveButton">Save</button>
        </div>
      </div>
    `;
  }
  renderCreatePopup() {
    if (!this.showCreatePopup) return b2``;
    return b2`
      <div class="createAppointmentPopup">
        <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">Create Appointment</p>
        
        <label>Search User (Last, First):</label>
        <input 
          type="text" 
          .value="${this.formUserName}" 
          @input="${this.handleUserSearchInput}" 
          placeholder="Type user name to search..."
        >
        ${this.userSearchResults.length > 0 ? b2`
          <div class="user-search-results">
            ${this.userSearchResults.map((user) => b2`
              <div class="user-search-item" @click="${() => this.selectUser(user)}">
                ${user.name} (ID: ${user.id})
              </div>
            `)}
          </div>
        ` : ""}

        <label>Selected User ID: ${this.formUserId ? this.formUserId : "None"}</label>

        <label style="margin-top: 12px;">Appointment Time:</label>
        <input 
          type="datetime-local" 
          .value="${this.formTime}" 
          @input="${(e4) => this.formTime = e4.target.value}"
        >

        ${this.editAppointmentPopupVars()}

        <button @click=${this.editAppointmentAddVariable} class="btn-secondary" style="margin-top: 8px;">
          Add Variable
        </button>

        <div class="buttonContainer">
          <button @click="${this.cancelCreateAppointment}" class="cancelButton">Cancel</button>
          <button @click="${this.createAppointment}" class="saveButton">Create</button>
        </div>
      </div>
    `;
  }
  renderDeletePopup() {
    if (!this.showDeletePopup) return b2``;
    return b2`
      <div class="deleteAppointmentPopup">
        <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">Delete Appointment?</p>
        <div class="buttonContainer">
          <button @click="${this.cancelDeleteAppointment}" class="cancelButton">Cancel</button>
          <button @click="${this.deleteAppointment}" class="deleteButton">Delete</button>
        </div>
      </div>
    `;
  }
  async handleUserSearchInput(e4) {
    const query = e4.target.value;
    this.formUserName = query;
    if (query.trim() === "") {
      this.userSearchResults = [];
      return;
    }
    try {
      let res = await fetch(`/api/users/search?partName=${encodeURIComponent(query)}`);
      let data = await res.json();
      this.userSearchResults = data.content || [];
    } catch (err) {
      console.error("User search error:", err);
    }
  }
  selectUser(user) {
    this.formUserId = user.id;
    this.formUserName = user.name;
    this.userSearchResults = [];
  }
  editAppointmentAddVariable() {
    let newVariable = {
      varName: "",
      varContents: ""
    };
    this.newVarsArray.push(newVariable);
    this.requestUpdate();
  }
  removeVariable(index) {
    this.newVarsArray.splice(index, 1);
    this.requestUpdate();
  }
  editAppointmentPopupVars() {
    let htmlString = [];
    for (let i5 = 0; i5 < this.newVarsArray.length; i5++) {
      htmlString.push(b2`
        <div style="display: flex; gap: 8px; margin-top: 8px; align-items: flex-end;">
          <div style="flex: 1;">
            <label>Variable Name</label>
            <input
              class="newVarName"
              placeholder="Variable name"
              value="${this.newVarsArray[i5].varName}"
              @input=${(e4) => this.updateVarName(i5, e4)}
            >
          </div>
          <div style="flex: 1;">
            <label>Variable Contents</label>
            <input
              class="newVarContents"
              placeholder="Variable contents"
              value="${this.newVarsArray[i5].varContents}"
              @input=${(e4) => this.updateVarContents(i5, e4)}
            >
          </div>
          <button class="removeVarBtn" @click=${() => this.removeVariable(i5)}>X</button>
        </div>
      `);
    }
    return b2`${htmlString}`;
  }
  updateVarName(index, event) {
    this.newVarsArray[index].varName = event.target.value;
  }
  updateVarContents(index, event) {
    this.newVarsArray[index].varContents = event.target.value;
  }
  editAppointmentPopup() {
    this.formUserId = this.userId;
    this.formUserName = this.name;
    this.formTime = this.toLocalDatetimeInput(this.time);
    this.newVarsArray = this.vars.map((v2) => ({ ...v2 }));
    this.userSearchResults = [];
    this.showEditPopup = true;
  }
  cancelEditAppointment() {
    this.showEditPopup = false;
  }
  openCreatePopup() {
    this.formUserId = null;
    this.formUserName = "";
    this.formTime = "";
    this.newVarsArray = [];
    this.userSearchResults = [];
    this.showCreatePopup = true;
  }
  cancelCreateAppointment() {
    this.showCreatePopup = false;
  }
  async createAppointment() {
    if (!this.formUserId || !this.formTime) {
      return;
    }
    const instantTime = new Date(this.formTime).toISOString();
    let newAppointment = {
      userId: this.formUserId,
      time: instantTime,
      varList: this.newVarsArray
    };
    try {
      let response = await fetch("/api/appointments/create_appointment", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(newAppointment)
      });
      if (response.ok) {
        this.showCreatePopup = false;
        this.dispatchEvent(new CustomEvent("appointment-created", { bubbles: true, composed: true }));
      } else {
        console.error("Failed to create appointment");
      }
    } catch (error) {
      console.error("Error creating appointment:", error);
    }
  }
  async saveAppointment() {
    if (!this.formUserId || !this.formTime) {
      return;
    }
    const instantTime = new Date(this.formTime).toISOString();
    let updatedAppointment = {
      id: this.appointmentId,
      userId: this.formUserId,
      time: instantTime,
      varList: this.newVarsArray
    };
    try {
      let response = await fetch("/api/appointments/update_appointment", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(updatedAppointment)
      });
      if (response.ok) {
        let data = await response.json();
        this.showEditPopup = false;
        if (data.userId) this.userId = data.userId;
        if (data.time) this.time = data.time;
        if (data.varList) this.vars = data.varList;
        this.updateAppointment();
      } else {
        console.error("Failed to update appointment");
      }
    } catch (error) {
      console.error("Error updating appointment:", error);
    }
  }
  deleteAppointmentPopup() {
    this.showDeletePopup = true;
  }
  cancelDeleteAppointment() {
    this.showDeletePopup = false;
  }
  async deleteAppointment() {
    if (this.appointmentId == null) return;
    try {
      let response = await fetch(`/api/appointments/delete_appointment?id=${this.appointmentId}`, {
        method: "DELETE"
      });
      if (response.ok) {
        this.remove();
      } else {
        console.error("Failed to delete appointment");
      }
    } catch (error) {
      console.error("Error deleting appointment:", error);
    }
  }
  async textLogsPopup() {
    if (this.appointmentId == null) return;
    try {
      let response = await fetch(`/api/sms-logs/appointment?appointmentId=${this.appointmentId}`);
      if (response.ok) {
        this.smsLogs = await response.json();
      } else {
        this.smsLogs = [];
      }
    } catch (error) {
      console.error("Error fetching SMS logs:", error);
      this.smsLogs = [];
    }
    this.showTextLogPopup = true;
  }
  closeTextLogsPopup() {
    this.showTextLogPopup = false;
  }
  renderTextLogsPopup() {
    if (!this.showTextLogPopup) return b2``;
    return b2`
      <div class="textLogsPopup">
        <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">SMS Chat Logs</p>
        
        <div class="chat-container">
          ${this.smsLogs.length === 0 ? b2`<p style="text-align: center; color: var(--text-muted); margin: auto;">No message logs found.</p>` : this.smsLogs.map((log) => b2`
              <div class="chat-bubble ${log.direction ? log.direction.toLowerCase() : "outbound"}">
                <div>${log.messageContent || b2`<span style="font-style: italic; opacity: 0.7;">[Empty Message]</span>`}</div>
                <div class="chat-timestamp">${this.formatReadableTime(log.timestamp)}</div>
              </div>
            `)}
        </div>

        <div class="buttonContainer">
          <button @click="${this.closeTextLogsPopup}" class="cancelButton">Close</button>
        </div>
      </div>
    `;
  }
  render() {
    return b2`
      <div class="appointmentContainer">
        ${this.renderDeletePopup()}
        ${this.renderEditPopup()}
        ${this.renderCreatePopup()}
        ${this.renderTextLogsPopup()}
        ${this.renderStatusIndicator()}
        <p><strong>Name:</strong> ${this.name}</p>
        <p><strong>Time:</strong> ${this.formatReadableTime(this.time)}</p>
        ${this.varResolver()}
        <div class="buttonContainer">
          <button @click="${this.textLogsPopup}" class="showTextLogs">Text Logs</button>
          <button @click="${this.editAppointmentPopup}" class="editAppointment">Edit</button>
          <button @click="${this.deleteAppointmentPopup}" class="deleteAppointment">Delete</button>
        </div>
      </div>
    `;
  }
};
customElements.define("appointment-card", AppointmentCard);
export {
  AppointmentCard
};
/*! Bundled license information:

@lit/reactive-element/css-tag.js:
  (**
   * @license
   * Copyright 2019 Google LLC
   * SPDX-License-Identifier: BSD-3-Clause
   *)

@lit/reactive-element/reactive-element.js:
lit-html/lit-html.js:
lit-element/lit-element.js:
  (**
   * @license
   * Copyright 2017 Google LLC
   * SPDX-License-Identifier: BSD-3-Clause
   *)

lit-html/is-server.js:
  (**
   * @license
   * Copyright 2022 Google LLC
   * SPDX-License-Identifier: BSD-3-Clause
   *)
*/
//# sourceMappingURL=appointment-card.js.map
