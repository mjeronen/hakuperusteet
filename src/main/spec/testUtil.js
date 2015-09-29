import $ from 'jquery'
import Q from 'q'

const testTimeoutPageLoad = 120000
const waitIntervalMs = 10

export function S(selector) {
  try {
    if (!testFrame()) {
      return $([])
    }
    return $(testFrame().document).find(selector)
  } catch (e) {
    console.log("Premature access to testFrame.jQuery, printing stack trace.")
    console.log(new Error().stack)
    throw e
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
  return $("#testframe").get(0).contentWindow.SESSION_INITED_FOR_TESTING === true
}

export function testFrame() {
  return $("#testframe").get(0).contentWindow
}

export function openPage(path, predicate) {
  if (!predicate) {
    predicate = function () {
      return testFrame().jQuery
    }
  }
  return function () {
    var newTestFrame = $('<iframe>').attr({src: path, width: 1024, height: 800, id: "testframe"})
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

export function takeScreenshot() {
  if (window.callPhantom) {
    var date = new Date()
    var filename = "target/screenshots/" + date.getTime()
    console.log("Taking screenshot " + filename + ".png")
    callPhantom({'screenshot': filename})
  }
}