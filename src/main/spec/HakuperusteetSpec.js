import 'mocha'
import {expect} from 'chai'
import $ from 'jquery'

import {openPage, hakuperusteetLoaded, testFrame, takeScreenshot, S} from './testUtil.js'

const testTimeoutDefault = 30000
mocha.ui('bdd')
mocha.timeout(testTimeoutDefault)

describe('Page without sessions', function() {
  before(openPage("https://localhost:18080/hakuperusteet", hakuperusteetLoaded))

  it('should show Google login button', function() {
    const googleAuth = S(".googleAuthentication.login")
    expect(googleAuth.length).to.equal(1)
  })
})