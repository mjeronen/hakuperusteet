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

    it('insert firstname', () => {
      S("#firstName").val("John")
    })

    it('submit should be disabled', assertSubmitDisabled)
  })

  after(logout)
})

function assertSubmitDisabled() {
  return S2("input[name='submit']").then(expectToBeDisabled).then(done).catch(done)
}

function expectToBeDisabled(e) {
  expect($(e).attr("disabled")).to.equal("disabled")
}
