import React from 'react'

import {resolveLang, translation} from '../assets-common/translations/translations.js'

export default class Header extends React.Component {
  render() {
    const controller = this.props.controller;
    const lang = resolveLang();
    const langLinks = ["en", "sv", "fi"].
        filter((s)=> s !== lang).
        map((s, i) => <li key={i}><a href="#" onClick={controller.changeLang(s)}>{translation("header.changeLang", s)}</a></li>)

    return <section id="header">
      <div className="headerContent">
        <a href="/"><img src={"/hakuperusteet/img/opintopolku_large-"+lang+".png"} /></a>
      </div>
      <div className="languageSelector">
        <ul>
          {langLinks}
        </ul>
      </div>
    </section>
  }
}