import React from 'react'

import {translation} from '../assets-common/translations/translations.js'

export default class Header extends React.Component {
  render() {
    const controller = this.props.controller
    return <section id="header">
      <div className="headerContent">
        <a href="https://studyinfo.fi/wp2/en/"><img src="/hakuperusteet/img/opintopolku_large-en.png" /></a>
      </div>
      <a id="changeLangEn" href="#" onClick={controller.changeLang("en")}>In English</a>
      <a id="changeLangSv" href="#" onClick={controller.changeLang("sv")}>På svenska</a>
      <a id="changeLangFi" href="#" onClick={controller.changeLang("fi")}>Suomeksi</a>
    </section>
  }
}