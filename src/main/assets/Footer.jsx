import React from 'react'

export default class Footer extends React.Component {
  render() {
    return <footer>
      <div>
        <div className="footer-logo">
          <a href="http://www.oph.fi/english" title="Finnish National Board of Education"><img alt="Finnish National Board of Education" src="/hakuperusteet/img/OPH_logo-en.png" /></a>
          <div className="info"><a href="mailto:applicationfee@studyinfo.fi">applicationfee@studyinfo.fi</a></div>
        </div>
        <div className="footer-logo">
          <a href="http://www.minedu.fi/OPM/?lang=en" title="Ministry of Education and Culture"><img alt="Ministry of Education and Culture" src="/hakuperusteet/img/OKM_logo-en.png" /></a>
        </div>
      </div>
    </footer>
  }
}