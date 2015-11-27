import React from 'react'
import _ from 'lodash'

import '../assets-common/css/hakuperusteet.less'
import './css/admin.less'
import './css/props.less'

import AdminForm from './HakuperusteetAdminForm.jsx'

export default class HakuperusteetPage extends React.Component {
    constructor(props) {
        super()
        this.changes = props.controller.pushSearchChange
    }

    render() {
        const state = this.props.state
        const controller = this.props.controller
        const users = _.isEmpty(state.users) ? [] : state.users
        const oppijaClassName = state.isSearching ? "sidebar oppija-search searching" : "sidebar oppija-search"
        const fullName = (user) => (user.firstName && user.lastName) ? <span>{user.firstName}&nbsp;{user.lastName}</span> : <span>{user.email}</span>

        const results = state.isSearching ? <ul></ul> : <ul>
                        {users.filter(u => {
                            if(_.isEmpty(state.userSearch)) {
                                return true
                            } else {
                                var name = (u.firstName + " " + u.lastName).toLowerCase()
                                return name.indexOf(state.userSearch.toLowerCase()) > -1
                            }
                        }).map((u, i) => {
                            const selected = u.id == state.id ? "selected user" : "user"
                            return <li key={i} className={selected}><a onClick={this.selectUser.bind(this, u)}>{fullName(u)}</a></li>;
                        })}
        </ul>

        return <div>
            <div className="content-area">
                <div className={oppijaClassName}>
                    <label htmlFor="userSearch">
                        <span>Opiskelija</span>
                        <input type="text" id="userSearch" name="userSearch" onChange={this.changes} onBlur={this.changes} maxLength="255" />
                    </label>
                    <div className="user-search">
                        {results}
                    </div>
                </div>
                <AdminForm state={state} controller={controller} />
            </div>
        </div>
    }

    selectUser(user) {
        const controller = this.props.controller
        history.pushState(null, null, `/hakuperusteetadmin/oppija/${user.personOid}`)
        controller.pushRouteChange(document.location.pathname)
    }
}
