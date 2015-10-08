import React from 'react'
import _ from 'lodash'

import style from '../assets-common/css/hakuperusteet.less'

import Header from './Header.jsx'
import Footer from '../assets-common/Footer.jsx'
import AdminForm from './HakuperusteetAdminForm.jsx'

export default class HakuperusteetPage extends React.Component {
    constructor(props) {
        super()
        this.changes = props.controller.valueChanges
    }

    render() {
        const state = this.props.state
        const controller = this.props.controller
        const users = state.users
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
        const controller = this.props.controller
        history.pushState(null, null, `/hakuperusteetadmin/oppija/${user.personOid}`)
        controller.pushRouteChange(document.location.pathname)
    }
}
