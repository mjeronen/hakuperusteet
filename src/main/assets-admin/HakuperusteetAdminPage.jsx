import React from 'react'
import _ from 'lodash'

import '../assets-common/css/hakuperusteet.less'
import './css/admin.less'
import './css/props.less'

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
            <div className="content-area">
                <div className="sidebar oppija-haku">
                    <label htmlFor="userSearch">
                        <span>Opiskelija</span>
                        <input type="text" id="userSearch" name="userSearch" onChange={this.changes} onBlur={this.changes} maxLength="255" />
                    </label>
                    <div className="hakutulokset">
                        <ul>
                        {users.map((u, i) => {
                            const selected = u.id == state.id ? "selected" : null
                            return <li key={i} className={selected}><a onClick={this.selectUser.bind(this, u)}>{u.firstName}&nbsp;{u.lastName}</a></li>;
                        })}
                        </ul>
                    </div>
                </div>
                <AdminForm state={state} controller={controller} />
            </div>

            <Footer />
        </div>
    }

    selectUser(user) {
        const controller = this.props.controller
        history.pushState(null, null, `/hakuperusteetadmin/oppija/${user.personOid}`)
        controller.pushRouteChange(document.location.pathname)
    }
}
