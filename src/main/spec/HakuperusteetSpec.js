import {expect, done} from 'chai'
import {resetServer, openPage, hakuperusteetLoaded, testFrame, logout, takeScreenshot, S, S2} from './testUtil.js'

describe('Page without session', () => {
  before(openPage("/hakuperusteet", hakuperusteetLoaded))

  it('should show Google login button', assertOneFound(".googleAuthentication.login"))
  it('should not show Google session', assertNotFound(".googleAuthentication.session"))
  it('should show Email login button', assertOneFound(".emailAuthentication.login"))
  it('should not show Email session', assertNotFound(".emailAuthentication.session"))
  it('should not show userDataForm', assertNotFound("#userDataForm"))
  it('should not show educationForm', assertNotFound("#educationForm"))
  it('should not show vetuma start', assertNotFound(".vetumaStart"))
  it('should not show hakuList', assertNotFound(".hakuList"))
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
    it('should show token order success', assertOneFound("#session .success"))
  })
})

describe('Page without session - invalid login token', () => {
  before(openPage("/hakuperusteet/#/token/nonExistingToken", hakuperusteetLoaded))

  it('should show login error message', assertOneFound(".authentication-error"))
})

describe('Page with email session - userdata', () => {
  before(resetServer)
  before(openPage("/hakuperusteet/ao/1.2.246.562.20.69046715533/#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', assertOneFound(".loggedInAs"))
  it('should not show email login button', assertNotFound(".emailAuthentication.login"))
  it('should not show Google login button', assertNotFound(".googleAuthentication.login"))
  it('should not show Google session', assertNotFound(".googleAuthentication.session"))
  it('should show logout button', assertOneFound("#logout"))

  it('should show userDataForm', assertOneFound("#userDataForm"))
  it('should not show educationForm', assertNotFound("#educationForm"))
  it('should not show vetuma start', assertNotFound(".vetumaStart"))
  it('should not show hakuList', assertNotFound(".hakuList"))

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
    it('should not show missing errors', assertNotFound("#userDataForm .error"))

    it('select personId', () => { clickField("#hasPersonId") })
    it('submit should be disabled', assertSubmitDisabled)
    it('show one error after personId is clicked', assertOneFound("#userDataForm .error"))

    it('insert birthDate', () => { return setField("#personId", "-9358") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', assertNotFound("#userDataForm .error"))
  })

  describe('Submit userDataForm', () => {
    it('click submit should post userdata', () => { clickField("input[name='submit']") })
    it('should open educationForm after submit', assertOneFound("#educationForm"))
  })
})

describe('Page with email session - educationdata', () => {
  it('should not show userDataForm', assertNotFound("#userDataForm"))
  it('should show educationForm', assertOneFound("#educationForm"))
  it('should not show vetuma start', assertNotFound(".vetumaStart"))
  it('should not show hakuList', assertNotFound(".hakuList"))

  describe('Insert data', () => {
    it('initially submit should be disabled', assertSubmitDisabled)
    it('initially show all missing errors', () => {
      return S2("#educationForm .error").then((e) => { expect(e.length).to.equal(2) }).then(done).catch(done)
    })

    it('select educationLevel', () => { return setField("#educationLevel", "116") })
    it('submit should be disabled', assertSubmitDisabled)

    it('select educationCountry - Finland', () => { return setField("#educationCountry", "246") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', assertNotFound("#educationForm .error"))
    it('noPaymentRequired should be visible', assertOneFound(".noPaymentRequired"))
    it('paymentRequired should be hidden', assertNotFound(".paymentRequired"))
    it('alreadyPaid should be hidden', assertNotFound(".alreadyPaid"))

    it('select educationCountry - Solomin Islands', () => { return setField("#educationCountry", "090") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', assertNotFound("#educationForm .error"))
    it('paymentRequired should be visible', assertOneFound(".paymentRequired"))
    it('noPaymentRequired should be hidden', assertNotFound(".noPaymentRequired"))
    it('alreadyPaid should be hidden', assertNotFound(".alreadyPaid"))

    describe('Submit educationForm', () => {
      it('click submit should post educationdata', () => { clickField("input[name='submit']") })
      it('should show vetuma startpage after submit', assertOneFound(".vetumaStart"))
    })
  })
})

describe('Page with email session - vetuma start page', () => {
  it('should not show userDataForm', assertNotFound("#userDataForm"))
  it('should not show educationForm', assertNotFound("#educationForm"))
  it('should show vetuma start', assertOneFound(".vetumaStart"))
  it('should not show hakuList', assertNotFound(".hakuList"))

  // input name=submit is not allowed when doing redirect, hence different name than in other forms
  it('initially submit should be enabled', () => { return S2("input[name='submitVetuma']").then(expectToBeEnabled).then(done).catch(done) })

  describe('Submit vetumaForm', () => {
    it('click submit should go to vetuma and return back with successful payment', () => { clickField("input[name='submitVetuma']") })
    it('should show successful payment as result', assertOneFound(".vetumaResult"))
    it('redirectForm should be visible', assertOneFound(".redirectToForm"))
  })
})

describe('Page with email session - hakulist page', () => {
  it('should not show userDataForm', assertNotFound("#userDataForm"))
  it('should not show educationForm', assertNotFound("#educationForm"))
  it('should not show vetuma start', assertNotFound(".vetumaStart"))
  it('should show hakuList', assertOneFound(".hakuList"))

  it('initially submit should be enabled', () => { return S2("input[name='redirectToForm']").then(expectToBeEnabled).then(done).catch(done)})

  describe('Submit hakulist form', () => {
    it('click submit should redirect to form', () => { clickField("input[name='redirectToForm']") })
    it('should show mock form', assertOneFound(".mockRedirect"))
  })
})

describe('Page with email session - add second application object', () => {
  before(openPage("/hakuperusteet/ao/1.2.246.562.20.31077988074#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', assertOneFound(".loggedInAs"))
  it('should not show userDataForm', assertNotFound("#userDataForm"))
  it('should show educationForm', assertOneFound("#educationForm"))
  it('should not show vetuma start', assertNotFound(".vetumaStart"))
  it('should not show hakuList', assertNotFound(".hakuList"))

  describe('Insert data', () => {
    it('initially submit should be disabled', assertSubmitDisabled)
    it('initially show all missing errors', () => {
      return S2("#educationForm .error").then((e) => { expect(e.length).to.equal(2) }).then(done).catch(done)
    })

    it('select educationLevel', () => { return setField("#educationLevel", "100") })
    it('submit should be disabled', assertSubmitDisabled)

    it('select educationCountry - Finland', () => { return setField("#educationCountry", "246") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', assertNotFound("#educationForm .error"))
    it('noPaymentRequired should be visible', assertOneFound(".noPaymentRequired"))
    it('paymentRequired should be hidden', assertNotFound(".paymentRequired"))
    it('alreadyPaid should be hidden', assertNotFound(".alreadyPaid"))

    it('select educationCountry - Solomin Islands', () => { return setField("#educationCountry", "090") })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', assertNotFound("#educationForm .error"))
    it('noPaymentRequired should be hidden', assertNotFound(".noPaymentRequired"))
    it('paymentRequired should be hidden', assertNotFound(".paymentRequired"))
    it('alreadyPaid should be displayed', assertOneFound(".alreadyPaid"))

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

  it('should show email as loggedIn user', assertOneFound(".loggedInAs"))
  it('should not show userDataForm', assertNotFound("#userDataForm"))
  it('should not show educationForm', assertNotFound("#educationForm"))
  it('should not show vetuma start', assertNotFound(".vetumaStart"))
  it('should show hakuList', assertOneFound(".hakuList"))
})


function assertSubmitDisabled() { return S2("input[name='submit']").then(expectToBeDisabled).then(done).catch(done) }
function assertSubmitEnabled() { return S2("input[name='submit']").then(expectToBeEnabled).then(done).catch(done)}
function assertOneFound(selector) { return () => { return S2(selector).then(expectOneElementFound).then(done).catch(done) }}
function assertNotFound(selector) { return () => { expect(S(selector).length).to.equal(0) } }

function expectOneElementFound(e) { expect(e.length).to.equal(1)}
function expectToBeDisabled(e) { expect($(e).attr("disabled")).to.equal("disabled") }
function expectToBeEnabled(e) { expect($(e).attr("disabled")).to.equal(undefined) }

function setVal(val) { return (e) => { $(e).val(val).focus().blur() }}
function setField(field, val) { return S2(field).then(setVal(val)).then(done).catch(done) }
function clickField(field) { return S2(field).then((e) => { $(e).click() }).then(done).catch(done) }
