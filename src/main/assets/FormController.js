import Dispatcher from './Dispatcher'
import FormStateLoop from './FormStateLoop'

const dispatcher = new Dispatcher()
const events = {
  initialState: 'initialState'
}

export default class FormController {
  constructor(props) {
    this.propertiesP = props.propertiesP
    this.stateLoop = new FormStateLoop(dispatcher, events)
  }

  initialize() {
    return this.stateLoop.initialize(this)
  }
}