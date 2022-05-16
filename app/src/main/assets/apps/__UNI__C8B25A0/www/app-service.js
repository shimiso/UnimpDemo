if (typeof Promise !== "undefined" && !Promise.prototype.finally) {
  Promise.prototype.finally = function(callback) {
    const promise = this.constructor;
    return this.then((value) => promise.resolve(callback()).then(() => value), (reason) => promise.resolve(callback()).then(() => {
      throw reason;
    }));
  };
}
;
if (typeof uni !== "undefined" && uni && uni.requireGlobal) {
  const global = uni.requireGlobal();
  ArrayBuffer = global.ArrayBuffer;
  Int8Array = global.Int8Array;
  Uint8Array = global.Uint8Array;
  Uint8ClampedArray = global.Uint8ClampedArray;
  Int16Array = global.Int16Array;
  Uint16Array = global.Uint16Array;
  Int32Array = global.Int32Array;
  Uint32Array = global.Uint32Array;
  Float32Array = global.Float32Array;
  Float64Array = global.Float64Array;
  BigInt64Array = global.BigInt64Array;
  BigUint64Array = global.BigUint64Array;
}
;
if (uni.restoreGlobal) {
  uni.restoreGlobal(Vue, weex, plus, setTimeout, clearTimeout, setInterval, clearInterval);
}
(function(vue, shared) {
  "use strict";
  function isDebugMode() {
    return typeof __channelId__ === "string" && __channelId__;
  }
  function jsonStringifyReplacer(k, p) {
    switch (shared.toRawType(p)) {
      case "Function":
        return "function() { [native code] }";
      default:
        return p;
    }
  }
  function normalizeLog(type, filename, args) {
    if (isDebugMode()) {
      args.push(filename.replace("at ", "uni-app:///"));
      return console[type].apply(console, args);
    }
    const msgs = args.map(function(v) {
      const type2 = shared.toTypeString(v).toLowerCase();
      if (["[object object]", "[object array]", "[object module]"].indexOf(type2) !== -1) {
        try {
          v = "---BEGIN:JSON---" + JSON.stringify(v, jsonStringifyReplacer) + "---END:JSON---";
        } catch (e) {
          v = type2;
        }
      } else {
        if (v === null) {
          v = "---NULL---";
        } else if (v === void 0) {
          v = "---UNDEFINED---";
        } else {
          const vType = shared.toRawType(v).toUpperCase();
          if (vType === "NUMBER" || vType === "BOOLEAN") {
            v = "---BEGIN:" + vType + "---" + v + "---END:" + vType + "---";
          } else {
            v = String(v);
          }
        }
      }
      return v;
    });
    return msgs.join("---COMMA---") + " " + filename;
  }
  function formatAppLog(type, filename, ...args) {
    const res = normalizeLog(type, filename, args);
    res && console[type](res);
  }
  function requireNativePlugin(name) {
    return weex.requireModule(name);
  }
  var _export_sfc = (sfc, props) => {
    const target = sfc.__vccOpts || sfc;
    for (const [key, val] of props) {
      target[key] = val;
    }
    return target;
  };
  var testModule = requireNativePlugin("TestModule");
  const _sfc_main$1 = {
    data() {
      return {
        syncRet: {},
        systemInfo: "",
        angent: ""
      };
    },
    methods: {
      testAsyncFunc() {
        testModule.testAsyncFunc({
          "name": "unimp",
          "age": 1
        }, (ret) => {
          formatAppLog("log", "at pages/index/index.vue:36", ret);
        });
      },
      testSyncFunc() {
        var ret = testModule.testSyncFunc({
          "name": "unimp",
          "age": 1
        });
        this.syncRet = ret;
        formatAppLog("log", "at pages/index/index.vue:46", ret);
      },
      gotoNativePage() {
        testModule.gotoNativePage();
      },
      quitUnimp() {
        plus.runtime.quit();
      },
      gotoHostNativePage() {
        testModule.gotoHostNativePage();
      },
      getSystemInfo() {
        this.angent = plus.navigator.getUserAgent();
        var _this = this;
        uni.getSystemInfo({
          success: function(res) {
            _this.systemInfo = res;
            formatAppLog("log", "at pages/index/index.vue:63", _this.systemInfo.model);
          }
        });
      },
      openDocument() {
        uni.downloadFile({
          url: "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-dc-site/b3f1d0b0-5168-11eb-bd01-97bc1429a9ff.pdf",
          success: (res) => {
            uni.openDocument({
              filePath: res.tempFilePath,
              success: () => {
                formatAppLog("log", "at pages/index/index.vue:74", "\u6253\u5F00\u6587\u6863\u6210\u529F");
              }
            });
          }
        });
      }
    }
  };
  function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
    return vue.openBlock(), vue.createElementBlock("div", null, [
      vue.createElementVNode("button", {
        type: "primary",
        onClick: _cache[0] || (_cache[0] = (...args) => $options.testAsyncFunc && $options.testAsyncFunc(...args))
      }, "testAsyncFunc"),
      vue.createElementVNode("button", {
        type: "primary",
        onClick: _cache[1] || (_cache[1] = (...args) => $options.testSyncFunc && $options.testSyncFunc(...args))
      }, "testSyncFunc"),
      vue.createElementVNode("text", null, "syncRet:" + vue.toDisplayString(JSON.stringify($data.syncRet)), 1),
      vue.createElementVNode("button", {
        type: "primary",
        onClick: _cache[2] || (_cache[2] = (...args) => $options.gotoNativePage && $options.gotoNativePage(...args))
      }, "\u8DF3\u8F6C\u539F\u751FActivity"),
      vue.createElementVNode("button", {
        type: "primary",
        onClick: _cache[3] || (_cache[3] = (...args) => $options.gotoHostNativePage && $options.gotoHostNativePage(...args))
      }, "\u8DF3\u8F6C\u5BBF\u4E3B\u539F\u751FActivity\u5F39\u7A97"),
      vue.createElementVNode("button", {
        type: "primary",
        onClick: _cache[4] || (_cache[4] = (...args) => $options.quitUnimp && $options.quitUnimp(...args))
      }, "\u9000\u51FA\u5F53\u524D\u5C0F\u7A0B\u5E8F"),
      vue.createElementVNode("button", {
        type: "primary",
        onClick: _cache[5] || (_cache[5] = (...args) => $options.openDocument && $options.openDocument(...args))
      }, "\u6253\u5F00\u6587\u4EF6"),
      vue.createElementVNode("button", {
        type: "primary",
        onClick: _cache[6] || (_cache[6] = (...args) => $options.getSystemInfo && $options.getSystemInfo(...args))
      }, "\u83B7\u53D6\u7CFB\u7EDF\u4FE1\u606Fqqq"),
      vue.createElementVNode("div", null, "angent: " + vue.toDisplayString($data.angent), 1),
      vue.createElementVNode("div", null, "systemInfo: " + vue.toDisplayString($data.systemInfo), 1)
    ]);
  }
  var PagesIndexIndex = /* @__PURE__ */ _export_sfc(_sfc_main$1, [["render", _sfc_render], ["__file", "/Users/shims/works/HBuilderProjects/test-module/pages/index/index.vue"]]);
  __definePage("pages/index/index", PagesIndexIndex);
  const _sfc_main = {
    onLaunch: function() {
      formatAppLog("log", "at App.vue:4", "App Launch");
    },
    onShow: function() {
      formatAppLog("log", "at App.vue:7", "App Show");
    },
    onHide: function() {
      formatAppLog("log", "at App.vue:10", "App Hide");
    }
  };
  var App = /* @__PURE__ */ _export_sfc(_sfc_main, [["__file", "/Users/shims/works/HBuilderProjects/test-module/App.vue"]]);
  function createApp() {
    const app = vue.createVueApp(App);
    return {
      app
    };
  }
  const { app: __app__, Vuex: __Vuex__, Pinia: __Pinia__ } = createApp();
  uni.Vuex = __Vuex__;
  uni.Pinia = __Pinia__;
  __app__.provide("__globalStyles", __uniConfig.styles);
  __app__._component.mpType = "app";
  __app__._component.render = () => {
  };
  __app__.mount("#app");
})(Vue, uni.VueShared);
