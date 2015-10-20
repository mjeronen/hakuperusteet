import React from 'react'
import _ from 'lodash'

import EducationForm from './education/EducationForm.jsx'
import UserDataForm from './userdata/UserDataForm.jsx'
import PaymentForm from './payment/PaymentForm.jsx'

export default class HakuperusteetAdminForm extends React.Component {
    constructor(props) {
        super()
        this.changes = props.controller.pushEducationFormChanges
    }

    render() {
        const state = this.props.state
        const controller = this.props.controller
        const isUserSelected = state.id ? true : false
        const applicationObjects = _.isEmpty(state.applicationObjects) ? [] : state.applicationObjects
        const payments = _.isEmpty(state.payments) ? [] : state.payments
        if(isUserSelected) {
            return <section className="main-content oppija">
                <UserDataForm state={state} controller={controller} />
                <h3>Maksut</h3>
                {payments.map((payment,i) => {
                    return <PaymentForm key={i} state={state} controller={controller} payment={payment}/>
                })}
                <h3>Hakukohteet</h3>
                {applicationObjects.map((applicationObject,i) => {
                    return <EducationForm key={i} state={state} controller={controller} applicationObject={applicationObject}/>
                })}
            </section>
        } else {
            return <section/>;
        }
    }

}