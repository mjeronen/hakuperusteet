export default class FormStateTransitions {
  constructor(dispatcher, events) {
    this.dispatcher = dispatcher
    this.events = events
  }

  onInitialState(state, realInitialState) {
    return realInitialState
  }
}