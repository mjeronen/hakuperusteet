import {expect, done} from 'chai'
import {resetServer, openPage, pageLoaded, S, S2, select, wait} from './testUtil.js'

describe('Admin UI front', () => {
  before(openPage("/hakuperusteetadmin", pageLoaded(form => form.find(".user").length == 7)))
  describe('Search functionality', () => {
    it('insert should be able to filter with email', setField("#userSearch", "anni.annilainen@example.com"))
    it('should show only filtered user', wait.until(() => select(".user").length == 1))
  })
  describe('Modifying user data', () => {
    before(openPage("/hakuperusteetadmin/oppija/1.2.246.562.24.00000001000", pageLoaded(form => form.find("input[value='Annilainen']").length == 1)))
    it('should change name', setField("#firstName", "Emmi_" + getRandomNumber()))
    it('should change mother tongue', setField("#nativeLanguage", "AB"))
    it('submit should be enabled', assertSubmitEnabled("#userDataForm"))
    it('click submit should post changes', clickField("#userDataForm input[name='submit']"))
    it('submit should be disabled after post', assertSubmitDisabled("#userDataForm"))
  })
  describe('Modifying payment data', () => {
    before(openPage("/hakuperusteetadmin/oppija/1.2.246.562.24.00000001006", pageLoaded(form => form.find("input[value='Marjanen']").length == 1)))
    it('should payment status', setField("select[name='status']", "started", "cancel"))
    it('submit should be enabled', assertSubmitEnabled("form[id^='payment']"))
    it('click submit should post changes', clickField("form[id^='payment'] input[name='submit']"))
    it('submit should be enabled', assertSubmitDisabled("form[id^='payment']"))
  })
  describe('Modifying application object', () => {
    before(openPage("/hakuperusteetadmin/oppija/1.2.246.562.24.00000001001", pageLoaded(form => form.find("input[value='Ossilainen']").length == 1)))
    it('submit should be disabled', assertSubmitDisabled(escape("#educationForm_1.2.246.562.20.69046715533")))
    it('change value of education', setField(escape("#educationCountry_1.2.246.562.20.69046715533"), "016", "010"))
    it('submit should be enabled', assertSubmitEnabled(escape("#educationForm_1.2.246.562.20.69046715533")))
    it('click submit should post changes', clickField(escape("#educationForm_1.2.246.562.20.69046715533") + " input[name='submit']"))
    it('submit should be disabled', assertSubmitDisabled(escape("#educationForm_1.2.246.562.20.69046715533")))
  })
})

function escape(str) {
  return (str+'').replace(/[.?*+^$[\]\\(){}|-]/g, "\\$&");
};

function getRandomNumber() {
  return Math.floor((Math.random() * 1000000) + 1);
}

function assertSubmitDisabled(form) { return () => S2(form + " input[name='submit']").then(expectToBeDisabled).then(done).catch(done) }
function assertSubmitEnabled(form) { return () => S2(form + " input[name='submit']").then(expectToBeEnabled).then(done).catch(done)}

function expectToBeDisabled(e) { expect($(e).attr("disabled")).to.equal("disabled") }
function expectToBeEnabled(e) { expect($(e).attr("disabled")).to.equal(undefined) }

function setVal(val) { return (e) => {
  $(e).focus().val(val).focus().blur().change()
}}
function setField(field, val, altval) {
  return wait.until(() => {
    const e = select(field)
    const ok = e.length > 0 ? true : false
    if(ok) {
      if(e.val() == val) {
        val = altval ? altval : val
      }
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
