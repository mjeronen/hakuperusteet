import {expect, done} from 'chai'
import {openPage, hakuperusteetLoaded, testFrame, logout, takeScreenshot, S, S2} from './testUtil.js'

describe('Page without session', function() {
  before(openPage("https://localhost:18080/hakuperusteet", hakuperusteetLoaded))

  it('should show Google login button', function() {
    return S2(".googleAuthentication.login").then((e) => {
      expect(e.length).to.equal(1)
    }).then(done).catch(done)
  })
  it('should not show Google session', function() {
    expect(S(".googleAuthentication.session").length).to.equal(0)
  })

  it('should show Email login button', function() {
    return S2(".emailAuthentication.login").then((e) => {
      expect(e.length).to.equal(1)
    }).then(done).catch(done)
  })

  it('should not show Email session', function() {
    expect(S(".emailAuthentication.session").length).to.equal(0)
  })

  it('should not show userDataForm', function() {
    expect(S("#userDataForm").length).to.equal(0)
  })

  it('should not show educationForm', function() {
    expect(S("#educationForm").length).to.equal(0)
  })

  it('should not show vetuma start', function() {
    expect(S(".vetumaStart").length).to.equal(0)
  })

  it('should not show hakuList', function() {
    expect(S(".hakuList").length).to.equal(0)
  })
})

describe('Page with email session - no userdata', function() {
  before(openPage("https://localhost:18080/hakuperusteet/#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', function () {
    return S2(".loggedInAs").then((e) => {
      expect(e.text()).to.equal("mochatest@example.com")
    }).then(done).catch(done)
  })

  it('should not show email login button', function() {
    expect(S(".emailAuthentication.login").length).to.equal(0)
  })

  it('should not show Google login button', function() {
    expect(S(".googleAuthentication.login").length).to.equal(0)
  })

  it('should not show Google session', function() {
    expect(S(".googleAuthentication.session").length).to.equal(0)
  })

  it('should show logout button', function () {
    return S2("#logout").then((e) => {
      expect(e.length).to.equal(1)
    }).then(done).catch(done)
  })

  it('should show userDataForm', function() {
    return S2("#userDataForm").then((e) => {
      expect(e.length).to.equal(1)
    }).then(done).catch(done)
  })

  it('should not show educationForm', function() {
    expect(S("#educationForm").length).to.equal(0)
  })

  it('should not show vetuma start', function() {
    expect(S(".vetumaStart").length).to.equal(0)
  })

  it('should not show hakuList', function() {
    expect(S(".hakuList").length).to.equal(0)
  })

  after(logout)
})