import React from 'react'
import _ from 'lodash'

import EducationForm from './education/EducationForm.jsx'
import UserDataForm from './userdata/UserDataForm.jsx'

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
        if(isUserSelected) {
        return <section className="main-content oppija">
            <UserDataForm state={state} controller={controller} />
            {applicationObjects.map((ao, i) => {
                return <EducationForm state={state} controller={controller} applicationObject={ao}/>
            })}
        </section>
        } else {
            return <section/>;
        }
    }

}