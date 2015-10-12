import React from 'react'

export default class AjaxLoader extends React.Component {
  render() {
    const hide = this.props.hide ? " hide" : ""
    const cssClass = hide ? "ajax-loader hide" : "ajax-loader"
    return <img className={cssClass} src="/hakuperusteetadmin/img/ajax-loader.gif" />
  }
}