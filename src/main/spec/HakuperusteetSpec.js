import {expect, done} from 'chai'
import {resetServer, openPage, hakuperusteetLoaded, testFrame, logout, takeScreenshot, S, S2} from './testUtil.js'

describe('Page without session', () => {
  before(openPage("/hakuperusteet", hakuperusteetLoaded))

  it('should show Google login button', () => {
    return S2(".googleAuthentication.login").then(assertOneElementFound).then(done).catch(done)
  })
  it('should not show Google session', expectNotFound(".googleAuthentication.session"))

  it('should show Email login button', () => {
    return S2(".emailAuthentication.login").then(assertOneElementFound).then(done).catch(done)
  })

  it('should not show Email session', expectNotFound(".emailAuthentication.session"))
  it('should not show userDataForm', expectNotFound("#userDataForm"))
  it('should not show educationForm', expectNotFound("#educationForm"))
  it('should not show vetuma start', expectNotFound(".vetumaStart"))
  it('should not show hakuList', expectNotFound(".hakuList"))
})

describe('Page without session - order email token', () => {
  before(openPage("/hakuperusteet/", hakuperusteetLoaded))

  it('submit should be disabled', assertSubmitDisabled)
  it('insert invalid email', () => { return setField("#emailToken", "asd@asd.fi asd2@asd.fi") })
  it('submit should be disabled', assertSubmitDisabled)

  it('insert valid email', () => { return setField("#emailToken", "asd@asd.fi") })
  it('submit should be enabled', assertSubmitEnabled)

  describe('Submit email token order', () => {
    it('click submit should post emailToken', () => { clickField("#session input[name='submit']") })
    it('should show token order success', () => {
      return S2("#session .success").then(assertOneElementFound).then(done).catch(done)
    })
  })
})

describe('Page without session - invalid login token', () => {
  before(openPage("/hakuperusteet/#/token/nonExistingToken", hakuperusteetLoaded))

  it('should show login error message', () => {
    return S2(".authentication-error").then(assertOneElementFound).then(done).catch(done)
  })
})

describe('Page with email session - userdata', () => {
  before(resetServer)
  before(openPage("/hakuperusteet/ao/1.2.246.562.20.69046715533/#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', () => {
    return S2(".loggedInAs").then(assertOneElementFound).then(done).catch(done)
  })

  it('should not show email login button', expectNotFound(".emailAuthentication.login"))
  it('should not show Google login button', expectNotFound(".googleAuthentication.login"))
  it('should not show Google session', expectNotFound(".googleAuthentication.session"))

  it('should show logout button', () => {
    return S2("#logout").then(assertOneElementFound).then(done).catch(done)
  })

  it('should show userDataForm', () => { return S2("#userDataForm").then(assertOneElementFound).then(done).catch(done) })
  it('should not show educationForm', expectNotFound("#educationForm"))
  it('should not show vetuma start', expectNotFound(".vetumaStart"))
  it('should not show hakuList', expectNotFound(".hakuList"))

  describe('Insert data', () => {
    it('initially submit should be disabled', assertSubmitDisabled)
    it('initially show all missing errors', () => {
      return S2("#userDataForm .error").then((e) => { expect(e.length).to.equal(6) }).then(done).catch(done)
    })

    it('insert firstName', () => { return setField("#firstName", "John") })
    it('submit should be disabled', assertSubmitDisabled)

    it('insert lastName', () => { return setField("#lastName", "Doe") })
    it('submit should be disabled', assertSubmitDisabled)

    it('insert birthDate', () => { return setField("#birthDate", "15051979") })
    it('submit should be disabled', assertSubmitDisabled)

    it('select gender', () => { return clickField("#gender-male") })
    it('submit should be disabled', assertSubmitDisabled)

    it('select nativeLanguage', () => { return setField("#nativeLanguage", "FI") })
    it('submit should be disabled', assertSubmitDisabled)

    it('select nationality', () => { return setField("#nationality", "246") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', expectNotFound("#userDataForm .error"))

    it('select personId', () => { clickField("#hasPersonId") })
    it('submit should be disabled', assertSubmitDisabled)
    it('show one error after personId is clicked', () => {
      return S2("#userDataForm .error").then(assertOneElementFound).then(done).catch(done)
    })

    it('insert birthDate', () => { return setField("#personId", "-9358") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', expectNotFound("#userDataForm .error"))
  })

  describe('Submit userDataForm', () => {
    it('click submit should post userdata', () => { clickField("input[name='submit']") })
    it('should open educationForm after submit', () => {
      return S2("#educationForm").then(assertOneElementFound).then(done).catch(done)
    })
  })
})

describe('Page with email session - educationdata', () => {
  it('should not show userDataForm', expectNotFound("#userDataForm"))
  it('should show educationForm', () => { return S2("#educationForm").then(assertOneElementFound).then(done).catch(done) })
  it('should not show vetuma start', expectNotFound(".vetumaStart"))
  it('should not show hakuList', expectNotFound(".hakuList"))

  describe('Insert data', () => {
    it('initially submit should be disabled', assertSubmitDisabled)
    it('initially show all missing errors', () => {
      return S2("#educationForm .error").then((e) => { expect(e.length).to.equal(2) }).then(done).catch(done)
    })

    it('select educationLevel', () => { return setField("#educationLevel", "116") })
    it('submit should be disabled', assertSubmitDisabled)

    it('select educationCountry - Finland', () => { return setField("#educationCountry", "246") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', expectNotFound("#educationForm .error"))
    it('noPaymentRequired should be visible', () => { return S2(".noPaymentRequired").then(assertOneElementFound).then(done).catch(done) })
    it('paymentRequired should be hidden', expectNotFound(".paymentRequired"))
    it('alreadyPaid should be hidden', expectNotFound(".alreadyPaid"))

    it('select educationCountry - Solomin Islands', () => { return setField("#educationCountry", "090") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', expectNotFound("#educationForm .error"))
    it('paymentRequired should be visible', () => { return S2(".paymentRequired").then(assertOneElementFound).then(done).catch(done)})
    it('noPaymentRequired should be hidden', expectNotFound(".noPaymentRequired"))
    it('alreadyPaid should be hidden', expectNotFound(".alreadyPaid"))

    describe('Submit educationForm', () => {
      it('click submit should post educationdata', () => { clickField("input[name='submit']") })
      it('should show vetuma startpage after submit', () => {
        return S2(".vetumaStart").then(assertOneElementFound).then(done).catch(done)
      })
    })
  })
})

describe('Page with email session - vetuma start page', () => {
  it('should not show userDataForm', expectNotFound("#userDataForm"))
  it('should not show educationForm', expectNotFound("#educationForm"))
  it('should show vetuma start', () => { return S2(".vetumaStart").then(assertOneElementFound).then(done).catch(done) })
  it('should not show hakuList', expectNotFound(".hakuList"))

  // input name=submit is not allowed when doing redirect, hence different name than in other forms
  it('initially submit should be enabled', () => { return S2("input[name='submitVetuma']").then(expectToBeEnabled).then(done).catch(done) })

  describe('Submit vetumaForm', () => {
    it('click submit should go to vetuma and return back with successful payment', () => { clickField("input[name='submitVetuma']") })
    it('should show successful payment as result', () => {
      return S2(".vetumaResult").then(assertOneElementFound).then(done).catch(done)
    })

    it('redirectForm should be visible', () => {
      return S2(".redirectToForm").then(assertOneElementFound).then(done).catch(done)
    })
  })
})

describe('Page with email session - hakulist page', () => {
  it('should not show userDataForm', expectNotFound("#userDataForm"))
  it('should not show educationForm', expectNotFound("#educationForm"))
  it('should not show vetuma start', expectNotFound(".vetumaStart"))
  it('should show hakuList', () => { return S2(".hakuList").then(assertOneElementFound).then(done).catch(done) })

  it('initially submit should be enabled', () => { return S2("input[name='redirectToForm']").then(expectToBeEnabled).then(done).catch(done)})

  describe('Submit hakulist form', () => {
    it('click submit should redirect to form', () => { clickField("input[name='redirectToForm']") })
    it('should show mock form', () => {
      return S2(".mockRedirect").then(assertOneElementFound).then(done).catch(done)
    })
  })
})

describe('Page with email session - add second application object', () => {
  before(openPage("/hakuperusteet/ao/1.2.246.562.20.31077988074#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', () => {
    return S2(".loggedInAs").then(assertOneElementFound).then(done).catch(done)
  })

  it('should not show userDataForm', expectNotFound("#userDataForm"))
  it('should show educationForm', () => { return S2("#educationForm").then(assertOneElementFound).then(done).catch(done) })
  it('should not show vetuma start', expectNotFound(".vetumaStart"))
  it('should not show hakuList', expectNotFound(".hakuList"))

  describe('Insert data', () => {
    it('initially submit should be disabled', assertSubmitDisabled)
    it('initially show all missing errors', () => {
      return S2("#educationForm .error").then((e) => { expect(e.length).to.equal(2) }).then(done).catch(done)
    })

    it('select educationLevel', () => { return setField("#educationLevel", "100") })
    it('submit should be disabled', assertSubmitDisabled)

    it('select educationCountry - Finland', () => { return setField("#educationCountry", "246") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', expectNotFound("#educationForm .error"))
    it('noPaymentRequired should be visible', () => { return S2(".noPaymentRequired").then(assertOneElementFound).then(done).catch(done) })
    it('paymentRequired should be hidden', expectNotFound(".paymentRequired"))
    it('alreadyPaid should be hidden', expectNotFound(".alreadyPaid"))

    it('select educationCountry - Solomin Islands', () => { return setField("#educationCountry", "090") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', expectNotFound("#educationForm .error"))
    it('noPaymentRequired should be hidden', expectNotFound(".noPaymentRequired"))
    it('paymentRequired should be hidden', expectNotFound(".paymentRequired"))
    it('alreadyPaid should be displayed', () => { return S2(".alreadyPaid").then(assertOneElementFound).then(done).catch(done)})

    describe('Submit educationForm', () => {
      it('click submit should post educationdata', () => { clickField("input[name='submit']") })
      it('should show to application objects on hakulist page', () => {
        return S2(".redirectToForm").then((e) => { expect(e.length).to.equal(2) }).then(done).catch(done)
      })
    })
  })
})

describe('Page with email session - no new ao but two existing', () => {
  before(openPage("/hakuperusteet/#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', () => { return S2(".loggedInAs").then(assertOneElementFound).then(done).catch(done) })
  it('should not show userDataForm', expectNotFound("#userDataForm"))
  it('should not show educationForm', expectNotFound("#educationForm"))
  it('should not show vetuma start', expectNotFound(".vetumaStart"))
  it('should show hakuList', () => { return S2(".hakuList").then(assertOneElementFound).then(done).catch(done) })
})

function assertOneElementFound(e) { expect(e.length).to.equal(1)}

function assertSubmitDisabled() { return S2("input[name='submit']").then(expectToBeDisabled).then(done).catch(done) }
function assertSubmitEnabled() { return S2("input[name='submit']").then(expectToBeEnabled).then(done).catch(done)}

function expectNotFound(selector) { return () => { expect(S(selector).length).to.equal(0) } }
function expectToBeDisabled(e) { expect($(e).attr("disabled")).to.equal("disabled") }
function expectToBeEnabled(e) { expect($(e).attr("disabled")).to.equal(undefined) }

function setVal(val) { return (e) => { $(e).val(val).focus().blur() }}
function setField(field, val) { return S2(field).then(setVal(val)).then(done).catch(done) }
function clickField(field) { return S2(field).then((e) => { $(e).click() }).then(done).catch(done) }
