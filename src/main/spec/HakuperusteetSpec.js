import {expect, done} from 'chai'
import {resetServer, openPage, hakuperusteetLoaded, testFrame, logout, takeScreenshot, S, S2} from './testUtil.js'

describe('Page without session', () => {
  before(openPage("/hakuperusteet", hakuperusteetLoaded))

  it('should show Google login button', () => {
    return S2(".googleAuthentication.login").then(assertOneElementFound).then(done).catch(done)
  })
  it('should not show Google session', () => {
    expect(S(".googleAuthentication.session").length).to.equal(0)
  })

  it('should show Email login button', () => {
    return S2(".emailAuthentication.login").then(assertOneElementFound).then(done).catch(done)
  })

  it('should not show Email session', () => {
    expect(S(".emailAuthentication.session").length).to.equal(0)
  })

  it('should not show userDataForm', () => {expect(S("#userDataForm").length).to.equal(0) })
  it('should not show educationForm', () => {expect(S("#educationForm").length).to.equal(0) })
  it('should not show vetuma start', () => { expect(S(".vetumaStart").length).to.equal(0) })
  it('should not show hakuList', () => { expect(S(".hakuList").length).to.equal(0)})
})

describe('Page without session - order email token', () => {
  before(openPage("/hakuperusteet/", hakuperusteetLoaded))

  it('submit should be disabled', assertSubmitDisabled)
  it('insert invalid email', () => { S("#emailToken").val("asd@asd.fi asd2@asd.fi").focus().blur() })
  it('submit should be disabled', assertSubmitDisabled)

  it('insert valid email', () => { S("#emailToken").val("asd@asd.fi").focus().blur() })
  it('submit should be enabled', assertSubmitEnabled)

  describe('Submit email token order', () => {
    it('click submit should post emailToken', () => {
      S("#session input[name='submit']").click()
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

  it('should not show email login button', () => {
    expect(S(".emailAuthentication.login").length).to.equal(0)
  })

  it('should not show Google login button', () => {
    expect(S(".googleAuthentication.login").length).to.equal(0)
  })

  it('should not show Google session', () => {
    expect(S(".googleAuthentication.session").length).to.equal(0)
  })

  it('should show logout button', () => {
    return S2("#logout").then(assertOneElementFound).then(done).catch(done)
  })

  it('should show userDataForm', () => { return S2("#userDataForm").then(assertOneElementFound).then(done).catch(done) })
  it('should not show educationForm', () => { expect(S("#educationForm").length).to.equal(0) })
  it('should not show vetuma start', () => { expect(S(".vetumaStart").length).to.equal(0) })
  it('should not show hakuList', () => { expect(S(".hakuList").length).to.equal(0)})

  describe('Insert data', () => {
    it('initially submit should be disabled', assertSubmitDisabled)
    it('initially show all missing errors', () => {
      return S2("#userDataForm .error").then((e) => { expect(e.length).to.equal(6) }).then(done).catch(done)
    })

    it('insert firstName', () => { S("#firstName").val("John").focus().blur() })
    it('submit should be disabled', assertSubmitDisabled)

    it('insert lastName', () => { S("#lastName").val("Doe").focus().blur() })
    it('submit should be disabled', assertSubmitDisabled)

    it('insert birthDate', () => { S("#birthDate").val("15051979").focus().blur() })
    it('submit should be disabled', assertSubmitDisabled)

    it('select gender', () => { S("#gender-male").click() })
    it('submit should be disabled', assertSubmitDisabled)

    it('select nativeLanguage', () => { S("#nativeLanguage").val("FI").focus().blur() })
    it('submit should be disabled', assertSubmitDisabled)

    it('select nationality', () => { S("#nationality").val("246").focus().blur() })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', () => { expect(S("#userDataForm .error").length).to.equal(0) })

    it('select personId', () => { S("#hasPersonId").click() })
    it('submit should be disabled', assertSubmitDisabled)
    it('show one error after personId is clicked', () => {
      return S2("#userDataForm .error").then(assertOneElementFound).then(done).catch(done)
    })

    it('insert birthDate', () => { S("#personId").val("-9358").focus().blur() })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', () => { expect(S("#userDataForm .error").length).to.equal(0) })
  })

  describe('Submit userDataForm', () => {
    it('click submit should post userdata', () => {
      S("input[name='submit']").click()
      return S2("#educationForm").then(assertOneElementFound).then(done).catch(done)
    })
  })
})

describe('Page with email session - educationdata', () => {
  it('should not show userDataForm', () => {expect(S("#userDataForm").length).to.equal(0) })
  it('should show educationForm', () => { return S2("#educationForm").then(assertOneElementFound).then(done).catch(done) })
  it('should not show vetuma start', () => { expect(S(".vetumaStart").length).to.equal(0) })
  it('should not show hakuList', () => { expect(S(".hakuList").length).to.equal(0)})

  describe('Insert data', () => {
    it('initially submit should be disabled', assertSubmitDisabled)
    it('initially show all missing errors', () => {
      return S2("#educationForm .error").then((e) => { expect(e.length).to.equal(2) }).then(done).catch(done)
    })

    it('select educationLevel', () => { S("#educationLevel").val("116").focus().blur() })
    it('submit should be disabled', assertSubmitDisabled)

    it('select educationCountry - Finland', () => { S("#educationCountry").val("246").focus().blur() })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', () => { expect(S("#educationForm .error").length).to.equal(0) })
    it('noPaymentRequired should be visible', () => { return S2(".noPaymentRequired").then(assertOneElementFound).then(done).catch(done) })
    it('paymentRequired should be hidden', () => { expect(S(".paymentRequired").length).to.equal(0) })
    it('alreadyPaid should be hidden', () => { expect(S(".alreadyPaid").length).to.equal(0) })

    it('select educationCountry - Solomin Islands', () => { S("#educationCountry").val("090").focus().blur() })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', () => { expect(S("#educationForm .error").length).to.equal(0) })
    it('paymentRequired should be visible', () => { return S2(".paymentRequired").then(assertOneElementFound).then(done).catch(done)})
    it('noPaymentRequired should be hidden', () => { expect(S(".noPaymentRequired").length).to.equal(0) })
    it('alreadyPaid should be hidden', () => { expect(S(".alreadyPaid").length).to.equal(0) })

    describe('Submit educationForm', () => {
      it('click submit should post educationdata', () => {
        S("input[name='submit']").click()
        return S2(".vetumaStart").then(assertOneElementFound).then(done).catch(done)
      })
    })
  })
})

describe('Page with email session - vetuma start page', () => {
  it('should not show userDataForm', () => {expect(S("#userDataForm").length).to.equal(0) })
  it('should not show educationForm', () => { expect(S("#educationForm").length).to.equal(0) })
  it('should show vetuma start', () => { return S2(".vetumaStart").then(assertOneElementFound).then(done).catch(done) })
  it('should not show hakuList', () => { expect(S(".hakuList").length).to.equal(0)})

  // input name=submit is not allowed when doing redirect, hence different name than in other forms
  it('initially submit should be enabled', () => { return S2("input[name='submitVetuma']").then(expectToBeEnabled).then(done).catch(done) })

  describe('Submit vetumaForm', () => {
    it('click submit should go to vetuma and return back with successful payment', () => {
      S("input[name='submitVetuma']").click()
      return S2(".vetumaResult").then(assertOneElementFound).then(done).catch(done)
    })

    it('redirectForm should be visible', () => {
      return S2(".redirectToForm").then(assertOneElementFound).then(done).catch(done)
    })
  })
})

describe('Page with email session - hakulist page', () => {
  it('should not show userDataForm', () => {expect(S("#userDataForm").length).to.equal(0) })
  it('should not show educationForm', () => { expect(S("#educationForm").length).to.equal(0) })
  it('should not show vetuma start', () => { expect(S(".vetumaStart").length).to.equal(0) })
  it('should show hakuList', () => { return S2(".hakuList").then(assertOneElementFound).then(done).catch(done) })

  it('initially submit should be enabled', () => { return S2("input[name='redirectToForm']").then(expectToBeEnabled).then(done).catch(done)})

  describe('Submit hakulist form', () => {
    it('click submit should redirect to form', () => {
      S("input[name='redirectToForm']").click()
      return S2(".mockRedirect").then(assertOneElementFound).then(done).catch(done)
    })
  })
})

describe('Page with email session - add second application object', () => {
  before(openPage("/hakuperusteet/ao/1.2.246.562.20.31077988074#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', () => {
    return S2(".loggedInAs").then(assertOneElementFound).then(done).catch(done)
  })

  it('should not show userDataForm', () => { expect(S("#userDataForm").length).to.equal(0) })
  it('should show educationForm', () => { return S2("#educationForm").then(assertOneElementFound).then(done).catch(done) })
  it('should not show vetuma start', () => { expect(S(".vetumaStart").length).to.equal(0) })
  it('should not show hakuList', () => { expect(S(".hakuList").length).to.equal(0)})

  describe('Insert data', () => {
    it('initially submit should be disabled', assertSubmitDisabled)
    it('initially show all missing errors', () => {
      return S2("#educationForm .error").then((e) => { expect(e.length).to.equal(2) }).then(done).catch(done)
    })

    it('select educationLevel', () => { S("#educationLevel").val("100").focus().blur() })
    it('submit should be disabled', assertSubmitDisabled)

    it('select educationCountry - Finland', () => { S("#educationCountry").val("246").focus().blur() })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', () => { expect(S("#educationForm .error").length).to.equal(0) })
    it('noPaymentRequired should be visible', () => { return S2(".noPaymentRequired").then(assertOneElementFound).then(done).catch(done) })
    it('paymentRequired should be hidden', () => { expect(S(".paymentRequired").length).to.equal(0) })
    it('alreadyPaid should be hidden', () => { expect(S(".alreadyPaid").length).to.equal(0) })

    it('select educationCountry - Solomin Islands', () => { S("#educationCountry").val("090").focus().blur() })
    it('submit should be enabled', assertSubmitEnabled)
    it('should not show missing errors', () => { expect(S("#educationForm .error").length).to.equal(0) })
    it('noPaymentRequired should be hidden', () => { expect(S(".noPaymentRequired").length).to.equal(0) })
    it('paymentRequired should be hidden', () => { expect(S(".paymentRequired").length).to.equal(0) })
    it('alreadyPaid should be displayed', () => { return S2(".alreadyPaid").then(assertOneElementFound).then(done).catch(done)})

    describe('Submit educationForm', () => {
      it('click submit should post educationdata', () => {
        S("input[name='submit']").click()
        return S2(".redirectToForm").then((e) => { expect(e.length).to.equal(2) }).then(done).catch(done)
      })
    })
  })

})

describe('Page with email session - no new ao but two existing', () => {
  before(openPage("/hakuperusteet/#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', () => { return S2(".loggedInAs").then(assertOneElementFound).then(done).catch(done) })
  it('should not show userDataForm', () => {expect(S("#userDataForm").length).to.equal(0) })
  it('should not show educationForm', () => { expect(S("#educationForm").length).to.equal(0) })
  it('should not show vetuma start', () => { expect(S(".vetumaStart").length).to.equal(0) })
  it('should show hakuList', () => { return S2(".hakuList").then(assertOneElementFound).then(done).catch(done) })
})

function assertOneElementFound(e) { expect(e.length).to.equal(1)}

function assertSubmitDisabled() { return S2("input[name='submit']").then(expectToBeDisabled).then(done).catch(done) }
function assertSubmitEnabled() { return S2("input[name='submit']").then(expectToBeEnabled).then(done).catch(done)}

function expectToBeDisabled(e) { expect($(e).attr("disabled")).to.equal("disabled") }
function expectToBeEnabled(e) { expect($(e).attr("disabled")).to.equal(undefined) }