import React from 'react'

import {resolveLang} from '../assets-common/translations/translations.js'

export default class Header extends React.Component {
  render() {
    const controller = this.props.controller;
    const lang = resolveLang();
    const langLinks = [];
    var langLink1, langLink2;
    if(lang!=="en") {
      langLinks.push(<li><a href="#" onClick={controller.changeLang("en")}>In English</a></li>)
    }
    if(lang!=="sv") {
      langLinks.push(<li><a href="#" onClick={controller.changeLang("sv")}>På svenska</a></li>)
    }
    if(lang!=="fi") {
      langLinks.push(<li><a href="#" onClick={controller.changeLang("fi")}>Suomeksi</a></li>)
    }
    [langLink1, langLink2] = langLinks;

    return <section id="header">
      <div className="headerContent">
        <a href="https://studyinfo.fi/wp2/en/"><img src="/hakuperusteet/img/opintopolku_large-en.png" /></a>
      </div>
      <div className="languageSelector">
        <ul>
          {langLink1}
          {langLink2}
        </ul>
      </div>
    </section>
  }
}