import {expect, done} from 'chai'
import {resetServer, openPage, hakuperusteetAdminLoaded, testFrame, S, S2, S3, select, wait} from './testUtil.js'

describe('Admin UI front', () => {
  before(openPage("/hakuperusteetadmin", hakuperusteetAdminLoaded))
  it('should show users', wait.until(() => select(".user").length == 7 ))
  describe('Search functionality', () => {
    it('insert should be able to filter with email', setField("#userSearch", "anni.annilainen@example.com"))
    it('should show only filtered user', wait.until(() => select(".user").length == 1))
    describe('Viewing user', () => {
      it('should click user name', clickField("a:textContains('Annilainen')"))
      it('submit should be disabled', assertSubmitDisabled("#userDataForm"))
      describe('Modifying user data', () => {
        it('should change name', setField("#firstName", "Emmi_" + getRandomNumber()))
        it('submit should be enabled', assertSubmitEnabled("#userDataForm"))
        it('click submit should post changes', clickField("#userDataForm input[name='submit']"))
        it('submit should be disabled after post', assertSubmitDisabled("#userDataForm"))
      })
      describe('Modifying application object', () => {
        it('submit should be disabled', assertSubmitDisabled("#educationForm_1\\.2\\.246\\.562\\.20\\.69046715533"))
      })
    })

  })
})

function getRandomNumber() {
  return Math.floor((Math.random() * 1000000) + 1);
}

function assertSubmitDisabled(form) {
  return () => S2(form + " input[name='submit']").then(expectToBeDisabled).then(done).catch(done) }
function assertSubmitEnabled(form) {
  return () => S2(form + " input[name='submit']").then(expectToBeEnabled).then(done).catch(done)}

function expectToBeDisabled(e) { expect($(e).attr("disabled")).to.equal("disabled") }
function expectToBeEnabled(e) { expect($(e).attr("disabled")).to.equal(undefined) }

function setVal(val) { return (e) => {
  $(e).focus().val(val).focus().blur().change()
}}
function setField(field, val) {
  return wait.until(() => {
    const e = select(field)
    const ok = e.length == 1 ? true : false
    if(ok) {
      e.focus().val(val).focus().blur().change()
    }
    return ok
})}
function clickField(field) {
  return wait.until(() => {
    const e = select(field)
    if(e.length == 1 && e.attr("disabled") === undefined) {
      e[0].click()
      return true
    } else {
      return false
    }
  })
}
