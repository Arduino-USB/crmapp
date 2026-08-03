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

// src/message-card.js
var MessageCard = class extends i4 {
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

    .messageContainer p {
      margin: 0 0 8px 0;
      color: var(--text-main);
      font-size: 0.9rem;
      line-height: 1.4;
    }   
    
    .messageContainer {
      background-color: var(--bg-surface);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-md);
      padding: 16px;
      display: flex;
      flex-direction: column;
      align-items: stretch;
      box-shadow: var(--shadow-sm);
      transition: box-shadow 0.2s ease, transform 0.2s ease;
    }

    .messageContainer:hover {
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.4), 0 4px 6px -2px rgba(0, 0, 0, 0.2);
    }

    .buttonContainer {
      display: flex;
      flex-direction: row; 
      justify-content: flex-end;
      gap: 8px;
      margin-top: 12px;
      padding-top: 10px;
      border-top: 1px solid var(--border-color);
    }

    .buttonContainer button {
      font-family: inherit;
      font-size: 0.8rem;
      font-weight: 500;
      padding: 6px 12px;
      border-radius: var(--radius-sm);
      border: none;
      cursor: pointer;
      transition: background-color 0.2s ease, transform 0.1s ease;
    }

    .buttonContainer button:active {
      transform: scale(0.98);
    }

    .editMessage {
      background-color: var(--primary-color);
      color: var(--text-light);
    }

    .editMessage:hover {
      background-color: var(--primary-hover);
    }

    .deleteMessage {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .deleteMessage:hover {
      background-color: var(--danger-hover);
    }

    .cancelButton {
      background-color: var(--secondary-color);
      color: var(--text-main);
      border: 1px solid var(--border-color);
    }

    .cancelButton:hover {
      background-color: var(--secondary-hover);
      color: var(--text-light);
    }

    .saveButton {
      background-color: #22c55e;
      color: var(--text-light);
    }

    .saveButton:hover {
      background-color: #16a34a;
    }

    .editMessagePopup input,
    .editMessagePopup select,
    .editMessagePopup textarea {
      width: 100%;
      padding: 8px 10px;
      background-color: #0f172a;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      color: var(--text-main);
      font-size: 0.85rem;
      box-sizing: border-box;
      margin-top: 4px;
      margin-bottom: 8px;
    }

    .editMessagePopup input:focus,
    .editMessagePopup select:focus,
    .editMessagePopup textarea:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
    }

    .editMessagePopup label {
      font-size: 0.8rem;
      color: var(--text-muted);
      font-weight: 500;
    }

    .editMessagePopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 24px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 550px;
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
    
    .deleteMessagePopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 24px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 350px;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      box-sizing: border-box;
      border: 1px solid var(--border-color);
      z-index: 1000;
      color: var(--text-main);
    }
    
    .deleteMessagePopup .deleteButton {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .block-container {
      background: #0f172a;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      padding: 12px;
      margin-bottom: 12px;
    }

    .block-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
    }

    .remove-block-btn {
      background: var(--danger-color);
      color: #fff;
      border: none;
      padding: 3px 8px;
      border-radius: 4px;
      font-size: 0.75rem;
      cursor: pointer;
    }

    .add-block-btn {
      background: var(--secondary-color);
      color: #fff;
      border: 1px solid var(--border-color);
      padding: 8px 10px;
      border-radius: var(--radius-sm);
      font-size: 0.8rem;
      cursor: pointer;
      margin-bottom: 16px;
      width: 100%;
    }

    .add-block-btn:hover {
      background: var(--secondary-hover);
    }

    /* IFELSE Component Row Layout Styles */
    .ifelse-row {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 6px;
    }

    .ifelse-row input[type="text"] {
      margin: 0;
      flex: 1;
    }

    .pill {
      background-color: var(--primary-color);
      color: var(--text-light);
      padding: 4px 10px;
      border-radius: 16px;
      font-size: 0.75rem;
      font-weight: 500;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      flex: 1;
      justify-content: center;
    }

    .row-delete-btn {
      background: var(--danger-color);
      color: #fff;
      border: none;
      width: 24px;
      height: 24px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 0.8rem;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .ifelse-actions {
      display: flex;
      gap: 8px;
      margin-top: 8px;
      margin-bottom: 12px;
    }

    .ifelse-actions button {
      background: var(--secondary-color);
      color: var(--text-main);
      border: 1px solid var(--border-color);
      padding: 4px 8px;
      border-radius: var(--radius-sm);
      font-size: 0.75rem;
      cursor: pointer;
    }

    .ifelse-actions button:hover {
      background: var(--secondary-hover);
      color: var(--text-light);
    }

    .preview-box {
      background: #1e293b;
      border: 1px dashed var(--border-color);
      padding: 8px;
      border-radius: var(--radius-sm);
      font-size: 0.8rem;
      color: var(--text-muted);
      margin-top: 8px;
    }

    .preview-box span {
      color: var(--text-main);
    }

    .linked-responses-container {
      display: flex;
      flex-direction: column;
      gap: 8px;
      margin-bottom: 16px;
    }

    .linked-response-row {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .linked-response-row select {
      margin: 0;
      flex: 1;
    }

    .link-new-btn {
      background: var(--secondary-color);
      color: var(--text-main);
      border: 1px solid var(--border-color);
      padding: 6px 12px;
      border-radius: var(--radius-sm);
      font-size: 0.8rem;
      cursor: pointer;
      align-self: flex-start;
    }

    .link-new-btn:hover {
      background: var(--secondary-hover);
      color: var(--text-light);
    }
  `;
  static properties = {
    messageId: { type: Number },
    dayMileStone: { type: Number },
    isBefore: { type: Boolean },
    textList: { type: Array },
    responseIds: { type: Array },
    allResponses: { type: Array },
    varTypes: { type: Array },
    showEditPopup: { state: true },
    showDeletePopup: { state: true },
    formMilestone: { state: true },
    formIsBefore: { state: true },
    formTextList: { state: true },
    formResponseIds: { state: true },
    previewStates: { state: true }
  };
  constructor() {
    super();
    this.messageId = null;
    this.dayMileStone = 0;
    this.isBefore = true;
    this.textList = [];
    this.responseIds = [];
    this.allResponses = [];
    this.varTypes = [];
    this.showEditPopup = false;
    this.showDeletePopup = false;
    this.formMilestone = 0;
    this.formIsBefore = true;
    this.formTextList = [];
    this.formResponseIds = [];
    this.previewStates = {};
    this.updateInterval = null;
  }
  connectedCallback() {
    super.connectedCallback();
    this.updateMessage();
    this.fetchAllResponses();
    this.fetchVarTypes();
    this.updateInterval = setInterval(() => {
      this.updateMessage();
    }, 1e3);
  }
  disconnectedCallback() {
    super.disconnectedCallback();
    clearInterval(this.updateInterval);
  }
  async fetchAllResponses() {
    try {
      let res = await fetch("/api/messages/responses/get_all_responses");
      if (res.ok) {
        this.allResponses = await res.json();
      }
    } catch (e4) {
      console.error("Failed to load responses list", e4);
    }
  }
  async fetchVarTypes() {
    try {
      let res = await fetch("/api/messages/get_vartypes");
      if (res.ok) {
        this.varTypes = await res.json();
      }
    } catch (e4) {
      console.error("Failed to load var types", e4);
    }
  }
  async updateMessage() {
    if (this.messageId == null) return;
    try {
      let response = await fetch(`/api/messages/get_message_by_id?id=${this.messageId}`);
      if (response.status === 404) {
        this.remove();
        return;
      }
      if (!response.ok) throw new Error("Server error");
      let msg = await response.json();
      this.dayMileStone = msg.dayMileStone ?? 0;
      this.isBefore = msg.isBefore ?? true;
      this.textList = msg.textList || [];
      this.responseIds = msg.responseIds || [];
    } catch (error) {
      console.log(error);
    }
  }
  getSummaryText() {
    if (!this.textList || this.textList.length === 0) return "No blocks content";
    return this.textList.map((block) => {
      if (block.mode === "TEXT") {
        return `[Text: ${block.plainText || ""}]`;
      } else {
        return `[IfElse: ${block.varName || "var"}]`;
      }
    }).join(" ");
  }
  renderEditPopup() {
    if (!this.showEditPopup) return b2``;
    return b2`
      <div class="editMessagePopup">
        <p style="font-size: 1.05rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 8px;">Edit Message</p>
        
        <label>Milestone (Days):</label>
        <input 
          type="number" 
          .value="${this.formMilestone}" 
          @input="${(e4) => this.formMilestone = parseInt(e4.target.value, 10) || 0}"
        >

        <label>Timing:</label>
        <select .value="${String(this.formIsBefore)}" @change="${(e4) => this.formIsBefore = e4.target.value === "true"}">
          <option value="true">Before Appointment</option>
          <option value="false">After Appointment</option>
        </select>

        <label style="margin-top: 8px; display: block; font-weight: 600;">Text Blocks:</label>
        ${this.formTextList.map((block, index) => b2`
          <div class="block-container">
            <div class="block-header">
              <label>Block #${index + 1} (${block.mode})</label>
              <button type="button" class="remove-block-btn" @click="${() => this.removeFormBlock(index)}">Remove</button>
            </div>
            
            <label>Mode:</label>
            <select .value="${block.mode}" @change="${(e4) => this.updateBlockField(index, "mode", e4.target.value)}">
              <option value="TEXT">TEXT</option>
              <option value="IFELSE">IFELSE</option>
            </select>

            ${block.mode === "TEXT" ? b2`
              <label>Plain Text:</label>
              <textarea 
                rows="2" 
                .value="${block.plainText || ""}" 
                @input="${(e4) => this.updateBlockField(index, "plainText", e4.target.value)}"
              ></textarea>
            ` : b2`
              <label>Var Type:</label>
              <select .value="${block.varType || ""}" @change="${(e4) => this.updateBlockField(index, "varType", e4.target.value)}">
                <option value="">Select VarType</option>
                ${this.varTypes.map((vt) => b2`<option value="${vt}">${vt}</option>`)}
              </select>

              <label>Var Name:</label>
              <input 
                type="text" 
                .value="${block.varName || ""}" 
                @input="${(e4) => this.updateBlockField(index, "varName", e4.target.value)}"
              >

              <label>If Text Layout:</label>
              <div style="margin-top: 4px;">
                ${this.renderIfTextRows(block, index)}
              </div>

              <div class="ifelse-actions">
                <button type="button" @click="${() => this.addIfTextSentence(index)}">+ Add Text</button>
                <button type="button" @click="${() => this.addIfTextVariable(index)}">+ Insert Variable</button>
              </div>

              <label>Else Text (Fallback):</label>
              <input 
                type="text" 
                .value="${block.elseText || ""}" 
                @input="${(e4) => this.updateBlockField(index, "elseText", e4.target.value)}"
              >

              <div class="preview-box">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
                  <strong>Preview:</strong>
                  <select style="width: auto; margin: 0; padding: 2px 6px; font-size: 0.75rem;" 
                    @change="${(e4) => this.setPreviewState(index, e4.target.value)}">
                    <option value="exists">Variable Exists</option>
                    <option value="missing">Variable Missing (Else)</option>
                  </select>
                </div>
                <span>${this.getBlockPreview(block, index)}</span>
              </div>
            `}
          </div>
        `)}

        <button type="button" class="add-block-btn" @click="${this.addFormBlock}">+ Add Text Block</button>

        <label>Associated Responses:</label>
        <div class="linked-responses-container">
          ${this.formResponseIds.map((resId, index) => b2`
            <div class="linked-response-row">
              <select @change="${(e4) => this.updateLinkedResponse(index, parseInt(e4.target.value, 10))}">
                <option value="">Select Response Trigger</option>
                ${this.allResponses.map((resp) => b2`
                  <option value="${resp.id}" ?selected="${resp.id === resId}">${resp.text}</option>
                `)}
              </select>
              <button type="button" class="row-delete-btn" @click="${() => this.removeLinkedResponse(index)}">×</button>
            </div>
          `)}
          <button type="button" class="link-new-btn" @click="${this.addLinkedResponse}">Link New Response</button>
        </div>

        <div class="buttonContainer">
          <button @click="${() => this.showEditPopup = false}" class="cancelButton">Cancel</button>
          <button @click="${this.saveMessage}" class="saveButton">Save</button>
        </div>
      </div>
    `;
  }
  renderIfTextRows(block, blockIndex) {
    const ifText = block.ifText || [];
    const insertIndexes = block.insertIndexes || [];
    let rows = [];
    let textCursor = 0;
    for (let i5 = 0; i5 <= ifText.length; i5++) {
      if (textCursor < ifText.length) {
        const currentTextIdx = textCursor;
        rows.push(b2`
          <div class="ifelse-row">
            <input type="text" .value="${ifText[currentTextIdx]}" 
              @input="${(e4) => this.updateIfTextString(blockIndex, currentTextIdx, e4.target.value)}">
            <button type="button" class="row-delete-btn" @click="${() => this.removeIfTextItem(blockIndex, currentTextIdx, "text")}">×</button>
          </div>
        `);
        textCursor++;
      }
      if (insertIndexes.includes(textCursor)) {
        const targetVarIndex = insertIndexes.indexOf(textCursor);
        rows.push(b2`
          <div class="ifelse-row">
            <div class="pill">
              <span>{${block.varName || "variable"}}</span>
            </div>
            <button type="button" class="row-delete-btn" @click="${() => this.removeIfTextItem(blockIndex, targetVarIndex, "var")}">×</button>
          </div>
        `);
      }
    }
    return b2`${rows}`;
  }
  addIfTextSentence(blockIndex) {
    const block = this.formTextList[blockIndex];
    const ifText = [...block.ifText || [], ""];
    this.updateBlockField(blockIndex, "ifText", ifText);
  }
  addIfTextVariable(blockIndex) {
    const block = this.formTextList[blockIndex];
    const ifText = block.ifText || [];
    const insertIndexes = [...block.insertIndexes || [], ifText.length];
    const updated = [...this.formTextList];
    updated[blockIndex] = { ...updated[blockIndex], insertIndexes };
    this.formTextList = updated;
  }
  updateIfTextString(blockIndex, textIndex, value) {
    const block = this.formTextList[blockIndex];
    const ifText = [...block.ifText || []];
    ifText[textIndex] = value;
    this.updateBlockField(blockIndex, "ifText", ifText);
  }
  removeIfTextItem(blockIndex, itemIndex, type) {
    const block = this.formTextList[blockIndex];
    let ifText = [...block.ifText || []];
    let insertIndexes = [...block.insertIndexes || []];
    if (type === "text") {
      ifText.splice(itemIndex, 1);
      insertIndexes = insertIndexes.map((idx) => idx > itemIndex ? idx - 1 : idx);
    } else {
      insertIndexes.splice(itemIndex, 1);
    }
    const updated = [...this.formTextList];
    updated[blockIndex] = { ...updated[blockIndex], ifText, insertIndexes };
    this.formTextList = updated;
  }
  setPreviewState(blockIndex, state) {
    this.previewStates = { ...this.previewStates, [blockIndex]: state };
  }
  getBlockPreview(block, blockIndex) {
    const state = this.previewStates[blockIndex] || "exists";
    if (state === "missing") {
      return block.elseText || "(No fallback text provided)";
    }
    const ifText = block.ifText || [];
    const insertIndexes = block.insertIndexes || [];
    let result = "";
    let textCursor = 0;
    for (let i5 = 0; i5 <= ifText.length; i5++) {
      if (textCursor < ifText.length) {
        result += (ifText[textCursor] || "") + " ";
        textCursor++;
      }
      if (insertIndexes.includes(textCursor)) {
        result += `[${block.varName || "variable"}] `;
      }
    }
    return result.trim() || "(Empty if-text content)";
  }
  addLinkedResponse() {
    this.formResponseIds = [...this.formResponseIds, ""];
  }
  updateLinkedResponse(index, value) {
    const updated = [...this.formResponseIds];
    updated[index] = value;
    this.formResponseIds = updated;
  }
  removeLinkedResponse(index) {
    this.formResponseIds = this.formResponseIds.filter((_2, i5) => i5 !== index);
  }
  renderDeletePopup() {
    if (!this.showDeletePopup) return b2``;
    return b2`
      <div class="deleteMessagePopup">
        <p style="font-size: 1.05rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 8px;">Delete Message?</p>
        <div class="buttonContainer">
          <button @click="${() => this.showDeletePopup = false}" class="cancelButton">Cancel</button>
          <button @click="${this.deleteMessage}" class="deleteButton">Delete</button>
        </div>
      </div>
    `;
  }
  addFormBlock() {
    this.formTextList = [
      ...this.formTextList,
      { mode: "TEXT", plainText: "", varType: "", varName: "", ifText: [""], insertIndexes: [], elseText: "" }
    ];
  }
  removeFormBlock(index) {
    this.formTextList = this.formTextList.filter((_2, i5) => i5 !== index);
  }
  updateBlockField(index, field, value) {
    const updated = [...this.formTextList];
    updated[index] = { ...updated[index], [field]: value };
    this.formTextList = updated;
  }
  openEditPopup() {
    this.formMilestone = this.dayMileStone;
    this.formIsBefore = this.isBefore;
    this.formTextList = JSON.parse(JSON.stringify(this.textList));
    if (this.formTextList.length === 0) {
      this.formTextList = [{ mode: "TEXT", plainText: "", varType: "", varName: "", ifText: [""], insertIndexes: [], elseText: "" }];
    }
    this.formResponseIds = [...this.responseIds];
    this.fetchAllResponses();
    this.fetchVarTypes();
    this.showEditPopup = true;
  }
  async saveMessage() {
    let updatedMessage = {
      id: this.messageId,
      dayMileStone: this.formMilestone,
      isBefore: this.formIsBefore,
      textList: this.formTextList,
      responseIds: this.formResponseIds.filter((id) => id)
    };
    try {
      let response = await fetch("/api/messages/update_message", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedMessage)
      });
      if (response.ok) {
        this.showEditPopup = false;
        this.updateMessage();
      } else {
        console.error("Failed to update message");
      }
    } catch (e4) {
      console.error("Error updating message:", e4);
    }
  }
  async deleteMessage() {
    if (this.messageId == null) return;
    try {
      let res = await fetch(`/api/messages/delete_message?id=${this.messageId}`, { method: "DELETE" });
      if (res.ok) {
        this.remove();
      }
    } catch (e4) {
      console.error("Error deleting message:", e4);
    }
  }
  render() {
    return b2`
      <div class="messageContainer">
        ${this.renderDeletePopup()}
        ${this.renderEditPopup()}
        <p><strong>Milestone:</strong> ${this.dayMileStone} days ${this.isBefore ? "before" : "after"}</p>
        <p><strong>Content:</strong> ${this.getSummaryText()}</p>
        <p><strong>Responses Linked:</strong> ${this.responseIds.length}</p>
        <div class="buttonContainer">
          <button @click="${this.openEditPopup}" class="editMessage">Edit</button>
          <button @click="${() => this.showDeletePopup = true}" class="deleteMessage">Delete</button>
        </div>
      </div>
    `;
  }
};
if (!customElements.get("message-card")) {
  customElements.define("message-card", MessageCard);
}

// src/messages-page.js
var MessagesPage = class extends i4 {
  static styles = i`
    :host {
      display: block;
      color: #f8fafc;
      padding: 16px;
    }

    .top-bar {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      margin-bottom: 20px;
    }

    .create-btn {
      padding: 8px 16px;
      background-color: #38bdf8;
      color: #0f172a;
      font-weight: 600;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }

    .create-btn:hover {
      background-color: #0ea5e9;
    }

    .cardContainer {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 16px;
    }

    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.6);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    }

    .modal {
      background-color: #1e293b;
      padding: 24px;
      border-radius: 8px;
      width: 100%;
      max-width: 500px;
      border: 1px solid #334155;
      color: #f8fafc;
      max-height: 85vh;
      overflow-y: auto;
    }

    .modal h3 {
      margin-top: 0;
      margin-bottom: 16px;
    }

    .form-group {
      margin-bottom: 14px;
    }

    .form-group label {
      display: block;
      margin-bottom: 4px;
      font-size: 0.85rem;
      color: #94a3b8;
    }

    .form-group input,
    .form-group select,
    .form-group textarea {
      width: 100%;
      padding: 8px;
      background-color: #0f172a;
      border: 1px solid #475569;
      color: #f8fafc;
      border-radius: 4px;
      box-sizing: border-box;
    }

    .error-text {
      color: #f87171;
      font-size: 0.8rem;
      margin-top: 4px;
    }

    .modal-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
      margin-top: 20px;
    }

    .modal-actions button {
      padding: 6px 12px;
      border-radius: 4px;
      cursor: pointer;
      border: none;
    }

    .cancel-btn {
      background-color: #475569;
      color: #f8fafc;
    }

    .submit-btn {
      background-color: #38bdf8;
      color: #0f172a;
      font-weight: 600;
    }

    .response-checkboxes {
      max-height: 120px;
      overflow-y: auto;
      background: #0f172a;
      border: 1px solid #475569;
      border-radius: 4px;
      padding: 8px;
      margin-top: 4px;
    }

    .response-checkbox-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 0.85rem;
      padding: 4px 0;
    }

    .block-container {
      background: #0f172a;
      border: 1px solid #475569;
      border-radius: 4px;
      padding: 10px;
      margin-bottom: 10px;
    }

    .block-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 6px;
    }

    .remove-block-btn {
      background: #ef4444;
      color: #fff;
      border: none;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 0.75rem;
      cursor: pointer;
    }

    .add-block-btn {
      background: #334155;
      color: #fff;
      border: 1px solid #475569;
      padding: 6px 10px;
      border-radius: 4px;
      font-size: 0.8rem;
      cursor: pointer;
      margin-bottom: 12px;
      width: 100%;
    }

    .add-block-btn:hover {
      background: #1e293b;
    }
  `;
  static properties = {
    messages: { state: true },
    allResponses: { state: true },
    varTypes: { state: true },
    isModalOpen: { state: true },
    formMilestone: { state: true },
    formIsBefore: { state: true },
    formTextList: { state: true },
    formResponseIds: { state: true },
    formError: { state: true }
  };
  constructor() {
    super();
    this.messages = [];
    this.allResponses = [];
    this.varTypes = [];
    this.isModalOpen = false;
    this.formMilestone = 0;
    this.formIsBefore = true;
    this.formTextList = [];
    this.formResponseIds = [];
    this.formError = "";
  }
  async connectedCallback() {
    super.connectedCallback();
    await this.loadData();
  }
  async loadData() {
    try {
      let [msgRes, respRes, varRes] = await Promise.all([
        fetch("/api/messages/get_all_messages"),
        fetch("/api/messages/responses/get_all_responses"),
        fetch("/api/messages/get_vartypes")
      ]);
      if (msgRes.ok) this.messages = await msgRes.json();
      if (respRes.ok) this.allResponses = await respRes.json();
      if (varRes.ok) this.varTypes = await varRes.json();
    } catch (e4) {
      console.error("Failed to load data", e4);
    }
  }
  openModal() {
    this.formMilestone = 0;
    this.formIsBefore = true;
    this.formTextList = [
      { mode: "TEXT", plainText: "", varType: "", varName: "", ifText: [], insertIndexes: [], elseText: "" }
    ];
    this.formResponseIds = [];
    this.formError = "";
    this.isModalOpen = true;
  }
  addFormBlock() {
    this.formTextList = [
      ...this.formTextList,
      { mode: "TEXT", plainText: "", varType: "", varName: "", ifText: [], insertIndexes: [], elseText: "" }
    ];
  }
  removeFormBlock(index) {
    this.formTextList = this.formTextList.filter((_2, i5) => i5 !== index);
  }
  updateBlockField(index, field, value) {
    const updated = [...this.formTextList];
    updated[index] = { ...updated[index], [field]: value };
    this.formTextList = updated;
  }
  handleCheckbox(id, checked) {
    if (checked) {
      if (!this.formResponseIds.includes(id)) {
        this.formResponseIds = [...this.formResponseIds, id];
      }
    } else {
      this.formResponseIds = this.formResponseIds.filter((rId) => rId !== id);
    }
  }
  async submitNewMessage(e4) {
    e4.preventDefault();
    this.formError = "";
    let payload = {
      dayMileStone: parseInt(this.formMilestone, 10) || 0,
      isBefore: this.formIsBefore,
      textList: this.formTextList,
      responseIds: this.formResponseIds
    };
    try {
      let res = await fetch("/api/messages/create_message", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      if (res.ok) {
        this.isModalOpen = false;
        this.loadData();
      } else {
        this.formError = "Failed to create message.";
      }
    } catch (e5) {
      console.error(e5);
      this.formError = "Network error.";
    }
  }
  render() {
    return b2`
      <div class="top-bar">
        <button class="create-btn" @click="${this.openModal}">Create New Message</button>
      </div>

      <div class="cardContainer">
        ${this.messages.length === 0 ? b2`<p>No messages found.</p>` : this.messages.map((m2) => b2`
          <message-card 
            messageId="${m2.id}" 
            dayMileStone="${m2.dayMileStone}" 
            ?isBefore="${m2.isBefore}"
          ></message-card>
        `)}
      </div>

      ${this.isModalOpen ? b2`
        <div class="modal-overlay">
          <div class="modal">
            <h3>Create New Message</h3>
            <form @submit="${this.submitNewMessage}">
              <div class="form-group">
                <label>Milestone (Days):</label>
                <input type="number" .value="${this.formMilestone}" @input="${(e4) => this.formMilestone = e4.target.value}" required />
              </div>

              <div class="form-group">
                <label>Timing:</label>
                <select .value="${String(this.formIsBefore)}" @change="${(e4) => this.formIsBefore = e4.target.value === "true"}">
                  <option value="true">Before Appointment</option>
                  <option value="false">After Appointment</option>
                </select>
              </div>

              <label style="margin-top: 8px; display: block; font-weight: 600; color: #94a3b8; font-size: 0.85rem;">Text Blocks:</label>
              ${this.formTextList.map((block, index) => b2`
                <div class="block-container">
                  <div class="block-header">
                    <label style="font-size: 0.8rem; color: #94a3b8;">Block #${index + 1}</label>
                    <button type="button" class="remove-block-btn" @click="${() => this.removeFormBlock(index)}">Remove</button>
                  </div>
                  
                  <div class="form-group" style="margin-bottom: 8px;">
                    <label>Mode:</label>
                    <select .value="${block.mode}" @change="${(e4) => this.updateBlockField(index, "mode", e4.target.value)}">
                      <option value="TEXT">TEXT</option>
                      <option value="IFELSE">IFELSE</option>
                    </select>
                  </div>

                  ${block.mode === "TEXT" ? b2`
                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>Plain Text:</label>
                      <textarea rows="2" .value="${block.plainText || ""}" @input="${(e4) => this.updateBlockField(index, "plainText", e4.target.value)}" required></textarea>
                    </div>
                  ` : b2`
                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>Var Type:</label>
                      <select .value="${block.varType || ""}" @change="${(e4) => this.updateBlockField(index, "varType", e4.target.value)}">
                        <option value="">Select VarType</option>
                        ${this.varTypes.map((vt) => b2`<option value="${vt}">${vt}</option>`)}
                      </select>
                    </div>

                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>Var Name:</label>
                      <input type="text" .value="${block.varName || ""}" @input="${(e4) => this.updateBlockField(index, "varName", e4.target.value)}" required />
                    </div>

                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>If Text (comma separated options):</label>
                      <input type="text" .value="${(block.ifText || []).join(", ")}" @input="${(e4) => this.updateBlockField(index, "ifText", e4.target.value.split(",").map((s4) => s4.trim()))}" />
                    </div>

                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>Else Text (Fallback):</label>
                      <input type="text" .value="${block.elseText || ""}" @input="${(e4) => this.updateBlockField(index, "elseText", e4.target.value)}" />
                    </div>
                  `}
                </div>
              `)}

              <button type="button" class="add-block-btn" @click="${this.addFormBlock}">+ Add Text Block</button>

              <div class="form-group">
                <label>Associated Responses:</label>
                <div class="response-checkboxes">
                  ${this.allResponses.map((resp) => b2`
                    <div class="response-checkbox-item">
                      <input 
                        type="checkbox" 
                        .checked="${this.formResponseIds.includes(resp.id)}"
                        @change="${(e4) => this.handleCheckbox(resp.id, e4.target.checked)}"
                      >
                      <span>[${resp.mode}] "${resp.text}"</span>
                    </div>
                  `)}
                </div>
              </div>

              ${this.formError ? b2`<div class="error-text">${this.formError}</div>` : ""}

              <div class="modal-actions">
                <button type="button" class="cancel-btn" @click="${() => this.isModalOpen = false}">Cancel</button>
                <button type="submit" class="submit-btn">Save</button>
              </div>
            </form>
          </div>
        </div>
      ` : ""}
    `;
  }
};
customElements.define("messages-page", MessagesPage);
export {
  MessagesPage
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
//# sourceMappingURL=messages-page.js.map
