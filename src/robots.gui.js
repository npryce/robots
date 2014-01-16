define(["modash", "react", "robots.cards", "robots.drag", "robots.edit"], function(_, React, cards, drag, edit) {
    var dom = React.DOM;
	var drag_gesture = drag.gesture();
	
	function uniqueCardId() {
		return _.uniqueId("card-");
	}

	function cardCompletesRow(editor) {
		return cards.isControlCard(editor.node());
	}
	
	function editorsToRows(editors) {
		return _.splitAfter(editors, cardCompletesRow);
	}
	
	function rowIsComplete(row) {
		return cardCompletesRow(_.last(row));
	}

	function rowsAreComplete(rows) {
		return _.isEmpty(rows) || rowIsComplete(_.last(rows));
	}

	var Card = React.createClass({
		displayName: "robots.gui.Card",
		
		render: function() {
			var card = this.props.editor.node();
			var attrs = {
				className: "card " + (cards.isAtomicCard(card) ? "action" : "control"),
				id: card.id,
				onClick: this.handleClick
			};
			
			return dom.div(_.extend(attrs, this.props.attrs), card.text);
		},
		handleClick: function() {
			this.props.onEdit(this.props.editor.remove());
		}
	});
	
	var CardLayout = React.createClass({
		displayName: "robots.gui.CardLayout",
		
		getInitialState: function() {
			return {program: this.props.program};
		},
		programChanged: function(new_program) {
			this.setState({program: new_program});
		},
		render: function() {
			return this.renderSequence(edit.editorsFor(this.state.program), edit.appenderFor(this.state.program));
		},
		renderSequence: function(editors, appender, key) {
			var card_required = editors.length === 0;
			var rows = editorsToRows(editors);
			
			return dom.div({className:"cardsequence", key: key},
				_.map(rows, this.renderRow),
				(rowsAreComplete(rows) ? dom.div({className:"cardrow", key: "appendrow"}, this.renderNewCardDropTarget(appender, card_required)) : []));
		},
		renderRow: function(row, i) {
			return dom.div({className:"cardrow", key: i},
				_.map(row, this.renderRowElement),
				(rowIsComplete(row) ? [] : this.renderNewCardDropTarget(_.last(row), false)));
		},
		renderRowElement: function(editor) {
			var card = editor.node();
			
			if (cards.isAtomicCard(card)) {
				return this.renderCard(editor, {id: card.id});
			}
			else {
				return dom.div({className:"cardgroup", id: card.id, key: card.id},
					this.renderCard(editor),
					this.renderSequence(editor.editorsForBranch("body"), editor.appenderForBranch("body")));
			}
		},
		renderCard: function(editor, extra_attrs) {
			return Card({editor: editor, attrs: extra_attrs, key: editor.node().id, onEdit: this.props.onEdit});
		},
		renderNewCardDropTarget: function(appender, required) {
			var onEdit = this.props.onEdit;
			
			return DropTarget({
				key: "append",
				required: required,
				onCardDropped: function(card) {
					onEdit(appender.insertAfter(card));
				}
			});
		}
	});
	
    var DropTarget = React.createClass({
		displayName: "robots.gui.DropTarget",
		
		render: function() {
			return dom.div({className: "cursor" + (this.props.required ? " required" : "")});
		},
        componentDidMount: function() {
			var n = this.getDOMNode();
			n.addEventListener("carddragin", this.cardDragIn);
			n.addEventListener("carddrop", this.cardDrop);
		},
		cardDragIn: function(ev) {
			drag.acceptDrop(ev);
		},
		cardDrop: function(ev) {
			this.props.onCardDropped(drag.data(ev));
		}
	});
	
	var CardStack = React.createClass({
		displayName: "robots.gui.CardStack",
		
		render: function() {
			return dom.div({className: "card " + this.props.category}, 
						   this.props.stack.text);
		},
		componentDidMount: function() {
			this.getDOMNode().addEventListener("carddragstart", this.cardDragStart);
			drag_gesture.bind(this.getDOMNode());
		},
		cardDragStart: function(ev) {
			drag.acceptDrag(ev, cards.newCard(this.props.stack, uniqueCardId()));
		}
	});
	
	var CardStackRow = React.createClass({
		displayName: "robots.gui.CardStackRow",
		
		render: function() {
			var category = this.props.category;
			
			return dom.div({id: category},
					 _.map(this.props.stacks, function(stack, id) {
							   return CardStack({category: category, stack: stack, key: id});
						   }));
		}
	});
	
	var CardStacks = React.createClass({
		displayName: "robots.gui.CardStacks",
		
		render: function() {
			return dom.div({},
				CardStackRow({category: "control", stacks: this.props.cards.repeat}),
				CardStackRow({category: "action", stacks: this.props.cards.action}));
		}
	});
	
    return {
		CardLayout: CardLayout,
		CardStacks: CardStacks
	};
});
