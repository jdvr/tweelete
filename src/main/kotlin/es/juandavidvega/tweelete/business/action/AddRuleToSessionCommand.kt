package es.juandavidvega.tweelete.business.action

import es.juandavidvega.tweelete.business.model.Rule

data  class AddRuleToSessionCommand(val sessionId: String, val rules: List<Rule>)
