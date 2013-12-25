define(["lodash", "react", "robots.drag"], function(_, React, drag) {
    var dom = React.DOM;
	
	var CardLayout = React.createClass({
		displayName: "robots.CardLayout",
		
		getInitialState: function() {
			return {program: this.props.program};
		},
		render: function() {
			return this.renderSequence(this.state.program);
		},
		renderCard: function(c, extra_attrs) {
			var attrs = {};
			attrs.className = "card " + (c.isAtomic ? "action" : "control"); // React doesn't support classList :-(
			attrs.key = c.id;
			_.extend(attrs, extra_attrs);
			return dom.div(attrs, c.text);
		},
		renderRowElement: function(c) {
			if (c.isAtomic) {
				return this.renderCard(c, {id: c.id});
			}
			else {
				return dom.div({className:"cardgroup", id: c.id},
					this.renderCard(c),
					this.renderSequence(c.body));
			}
		},
		renderRow: function(r) {
			var card_required = r.length == 0;
			
			return dom.div({className:"cardrow"},
				_.map(r, this.renderRowElement),
				(r.closed ? [] : [this.renderNewCardDropTarget(r.sequence, card_required)]));
		},
		renderSequence: function(s) {
			var card_required = s.rowcount() == 1 && s.lastRow().length == 0;
			
			return dom.div({className:"cardsequence"},
				_.map(s.rows, this.renderRow),
				(s.lastRow().closed ? [dom.div({className:"cardrow"}, this.renderNewCardDropTarget(s, card_required))] : []));
		},
		renderNewCardDropTarget: function(sequence, required) {
			var onNewCardDropped = this.props.onNewCardDropped;
			
			return DropTarget({
				action: "new",
				key: 'append',
				required: required,
				onCardDropped: function(stack) {
					onNewCardDropped(sequence, stack);
				}
			});
		}
	});
	
    var DropTarget = React.createClass({
		displayName: "robots.DropTarget",
		
		render: function() {
			return dom.div({className: "cursor" + (this.props.required ? " required" : "")});
		},
        componentDidMount: function() {
			var n = this.getDOMNode();
			n.addEventListener("carddragin", this.cardDragIn);
			n.addEventListener("carddrop", this.cardDrop);
		},
		cardDragIn: function(ev) {
			drag.accept(ev, drag.action(ev) == this.props.action);
		},
		cardDrop: function(ev) {
			this.props.onCardDropped(drag.data(ev));
		}
	});
	
    return {
		CardLayout: CardLayout
	};
});