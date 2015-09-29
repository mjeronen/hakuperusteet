import 'mocha'
import {expect} from 'chai'
import $ from 'jquery'

import {openPage, hakuperusteetLoaded, testFrame, logout, takeScreenshot, S} from './testUtil.js'

const testTimeoutDefault = 30000
mocha.ui('bdd')
mocha.timeout(testTimeoutDefault)

describe('Page without session', function() {
  before(openPage("https://localhost:18080/hakuperusteet", hakuperusteetLoaded))

  it('should show Google login button', function() {
    expect(S(".googleAuthentication.login").length).to.equal(1)
  })

  it('should not show Google session', function() {
    expect(S(".googleAuthentication.session").length).to.equal(0)
  })

  it('should show Email login button', function() {
    expect(S(".emailAuthentication.login").length).to.equal(1)
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

describe('Page with email session', function() {
  before(openPage("https://localhost:18080/hakuperusteet/#/token/mochaTestToken", hakuperusteetLoaded))

  it('should show email as loggedIn user', function () {
    expect(S(".loggedInAs").text()).to.equal("mochatest@example.com")
  })

  it('should show logout button', function () {
    expect(S("#logout").length).to.equal(1)
  })

  after(logout)
})