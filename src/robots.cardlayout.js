define(["lodash", "react", "robots.drag"], function(_, React, drag) {
    var dom = React.DOM;
	
	var CardLayout = React.createClass({
		displayName: "robots.cardlayout.CardLayout",
		
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
				return dom.div({className:"cardgroup", id: c.id, key: c.id},
					this.renderCard(c),
					this.renderSequence(c.body, "body"));
			}
		},
		renderRow: function(r, i) {
			var card_required = r.length == 0;
			
			return dom.div({className:"cardrow", key: i},
				_.map(r, this.renderRowElement),
				(r.closed ? [] : [this.renderNewCardDropTarget(r.sequence, card_required)]));
		},
		renderSequence: function(s, key) {
			var card_required = s.rowcount() == 1 && s.lastRow().length == 0;
			
			return dom.div({className:"cardsequence", key: key},
				_.map(s.rows, this.renderRow),
				(s.lastRow().closed ? [dom.div({className:"cardrow", key: "appendrow"}, this.renderNewCardDropTarget(s, card_required))] : []));
		},
		renderNewCardDropTarget: function(sequence, required) {
			var onNewCardDropped = this.props.onNewCardDropped;
			
			return DropTarget({
				action: "new",
				key: "append",
				required: required,
				onCardDropped: function(stack) {
					onNewCardDropped(sequence, stack);
				}
			});
		}
	});
	
    var DropTarget = React.createClass({
		displayName: "robots.cardlayout.DropTarget",
		
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
	
	
	var new_card_gesture = drag.gesture("new");
		   
	var CardStack = React.createClass({
		displayName: "robots.cardlayout.CardStack",
		
		render: function() {
			return dom.div({className: "card " + this.props.category},
						   this.props.stack.text);
		},
		componentDidMount: function() {
			d3.select(this.getDOMNode()).data([this.props.stack]).call(new_card_gesture);
		}
	});
	
	var CardStackRow = React.createClass({
		displayName: "robots.cardlayout.CardStackRow",
		
		render: function() {
			var category = this.props.category;
			
			return dom.div({id: category},
					 _.map(this.props.stacks, function(stack, id) {
							   return CardStack({category: category, stack: stack, key: id});
						   }));
		}
	});
	
	var CardStacks = React.createClass({
		displayName: "robots.cardlayout.CardStacks",
		
		render: function() {
			return dom.div({},
				CardStackRow({category: "control", stacks: this.props.cards.control}),
				CardStackRow({category: "action", stacks: this.props.cards.action}));
		}
	});
	
    return {
		CardLayout: CardLayout,
		CardStacks: CardStacks
	};
});