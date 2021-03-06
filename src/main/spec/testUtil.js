const waitIntervalMs = 10

export function S(selector) {
  try {
    if (!testFrame() || !testFrame().jQuery) {
      return $([])
    }
    return $(testFrame().document).find(selector)
  } catch (e) {
    console.log("Premature access to testFrame.jQuery, printing stack trace.")
    console.log(new Error().stack)
    throw e
  }
}

export function S2(selector) {
  var deferred = Q.defer()
  waitUntil(() => findSelector(selector)).then(function() {
    deferred.resolve($(testFrame().document).find(selector))
  })
  return deferred.promise
}

function findSelector(selector) {
  try {
    return $(testFrame().document).find(selector).length > 0
  } catch(exception){
    return false
  }
}

export function waitUntil(condition, maxWaitMs) {
  if (maxWaitMs == undefined) maxWaitMs = testTimeoutDefault;
  var deferred = Q.defer()
  var count = maxWaitMs / waitIntervalMs;

  (function waitLoop(remaining) {
    if (condition()) {
      deferred.resolve()
    } else if (remaining === 0) {
      deferred.reject("timeout of " + maxWaitMs + " in wait.until of condition:\n" + condition)
    } else {
      setTimeout(function () {
        waitLoop(remaining - 1)
      }, waitIntervalMs)
    }
  })(count)
  return deferred.promise
}

export function waitUntilFalse(condition, maxWaitMs) {
  return waitUntil(function () {
    return !condition()
  })
}

export function waitforMilliseconds(ms) {
  var deferred = Q.defer()
  setTimeout(function () {
    deferred.resolve()
  }, ms)
  return deferred.promise
}

export function hakuperusteetLoaded() {
  try {
    return testFrame().SESSION_INITED_FOR_TESTING === true
  } catch(exception){
    return false
  }
}

export function testFrame() {
  return $("#testframe").get(0).contentWindow
}

export function openPage(path, predicate) {
  if (!predicate) {
    predicate = function() { return testFrame().jQuery }
  }
  return function () {
    var newTestFrame = $('<iframe>').attr({src: path, width: 1024, height: 800, id: "testframe"}).load(function() {
      var jquery = document.createElement("script")
      jquery.type = "text/javascript"
      jquery.src = "//code.jquery.com/jquery-1.11.1.min.js"
      $(this).contents().find("head")[0].appendChild(jquery)
    })
    $("#testframe").replaceWith(newTestFrame)
    return waitUntil(function () {return predicate() }, testTimeoutPageLoad
    ).then(function () {
      window.uiError = null
      testFrame().onerror = function (err) {
        window.uiError = err;
      } // Hack: force mocha to fail on unhandled exceptions
    })
  }
}

export function logout() {
  return S2("#logout").then((e) => {
    try {
      e.get(0).click()
    } catch(e) {
      // ignore phantomjs logout problem for now
    }
  })
}

export function takeScreenshot() {
  if (window.callPhantom) {
    var date = new Date()
    var filename = "target/screenshots/" + date.getTime()
    console.log("Taking screenshot " + filename + ".png")
    callPhantom({'screenshot': filename})
  }
}