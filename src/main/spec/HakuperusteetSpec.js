import {expect, done} from 'chai'
import {openPage, hakuperusteetLoaded, testFrame, logout, takeScreenshot, S, S2} from './testUtil.js'

describe('Page without session', () => {
  before(openPage("/hakuperusteet", hakuperusteetLoaded))

  it('should show Google login button', () => {
    return S2(".googleAuthentication.login").then((e) => {
      expect(e.length).to.equal(1)
    }).then(done).catch(done)
  })
  it('should not show Google session', () => {
    expect(S(".googleAuthentication.session").length).to.equal(0)
  })

  it('should show Email login button', () => {
    return S2(".emailAuthentication.login").then((e) => {
      expect(e.length).to.equal(1)
    }).then(done).catch(done)
  })

  it('should not show Email session', () => {
    expect(S(".emailAuthentication.session").length).to.equal(0)
  })

  it('should not show userDataForm', () => {
    expect(S("#userDataForm").length).to.equal(0)
  })

  it('should not show educationForm', () => {
    expect(S("#educationForm").length).to.equal(0)
  })

  it('should not show vetuma start', () => {
    expect(S(".vetumaStart").length).to.equal(0)
  })

  it('should not show hakuList', () => {
    expect(S(".hakuList").length).to.equal(0)
  })
})

describe('Page with email session - no userdata', () => {
  before(openPage("/hakuperusteet/#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', () => {
    return S2(".loggedInAs").then((e) => {
      expect(e.text()).to.equal("mochatest@example.com")
    }).then(done).catch(done)
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
    return S2("#logout").then((e) => {
      expect(e.length).to.equal(1)
    }).then(done).catch(done)
  })

  it('should show userDataForm', () => {
    return S2("#userDataForm").then((e) => {
      expect(e.length).to.equal(1)
    }).then(done).catch(done)
  })

  it('should not show educationForm', () => {
    expect(S("#educationForm").length).to.equal(0)
  })

  it('should not show vetuma start', () => {
    expect(S(".vetumaStart").length).to.equal(0)
  })

  it('should not show hakuList', () => {
    expect(S(".hakuList").length).to.equal(0)
  })

  describe('Insert data', () => {
    it('initially submit should be disabled', assertSubmitDisabled)

    it('insert firstName', () => { S("#firstName").val("John").focus() })
    it('submit should be disabled', assertSubmitDisabled)

    it('insert lastName', () => { S("#lastName").val("Doe").focus() })
    it('submit should be disabled', assertSubmitDisabled)

    it('insert birthDate', () => { S("#birthDate").val("15051979").focus() })
    it('submit should be disabled', assertSubmitDisabled)

    it('select gender', () => { S("#gender-male").click() })
    it('submit should be disabled', assertSubmitDisabled)

    it('select nativeLanguage', () => { S("#nativeLanguage").val("FI").focus().blur() })
    it('submit should be disabled', assertSubmitDisabled)

    it('select nationality', () => { S("#nationality").val("246").focus().blur() })
    it('submit should be enabled', assertSubmitEnabled)

    it('select personId', () => { S("#hasPersonId").click() })
    it('submit should be disabled', assertSubmitDisabled)

    it('insert birthDate', () => { S("#personId").val("-9358").focus().blur() })
    it('submit should be enabled', assertSubmitEnabled)
  })

  describe('Submit userDataForm', () => {
    it('click submit should post userdata', () => {
      S("input[name='submit']").click()
      return S2("#educationForm").then((e) => {
        expect(e.length).to.equal(1)
      }).then(done).catch(done)
    })
  })

  after(logout)
})

function assertSubmitDisabled() { return S2("input[name='submit']").then(expectToBeDisabled).then(done).catch(done) }
function assertSubmitEnabled() { return S2("input[name='submit']").then(expectToBeEnabled).then(done).catch(done)}

function expectToBeDisabled(e) { expect($(e).attr("disabled")).to.equal("disabled") }
function expectToBeEnabled(e) { expect($(e).attr("disabled")).to.equal(undefined) }