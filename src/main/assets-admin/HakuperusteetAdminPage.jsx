import React from 'react'
import _ from 'lodash'

import style from '../assets-common/css/hakuperusteet.less'

import Header from './Header.jsx'
import Footer from '../assets-common/Footer.jsx'
import AdminForm from './HakuperusteetAdminForm.jsx'
import {navigateToUser,routeP} from "./router.js"

export const oppijaP = routeP.flatMap(route => {
    var match = route.match(new RegExp("oppija/(.*)"))
    if(match) {
        console.log("Lue hakijan tiedot kannasta " + match)
        return true
    } else {
        console.log("Hakija ei ole valittu!")
        return false
    }
}).toProperty()

export default class HakuperusteetPage extends React.Component {
    constructor(props) {
        super()
        this.changes = props.controller.valueChanges
    }

    render() {
        const state = this.props.state
        const controller = this.props.controller
        const users = state.users
        console.log(oppijaP)
        return <div>
            <Header />
                <div className="userDataFormRow">
                    <label htmlFor="userSearch">
                        <span>Opiskelija</span>
                        <input type="text" id="userSearchz" name="userSearch" onChange={this.changes} onBlur={this.changes} maxLength="255" />
                    </label>
                </div>
                <ul>
                    {users.map((u, i) => {
                        return <li key={i}><a onClick={this.selectUser.bind(this, u)}>{u.firstName}&nbsp;{u.lastName}</a></li>;
                    })}
                </ul>
                <p></p>
                <AdminForm state={state} controller={controller} />
            <Footer />
        </div>
    }

    selectUser(user) {
        navigateToUser(user)
    }
}
