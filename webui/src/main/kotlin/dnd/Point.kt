package dnd

data class Point(val x: Double, val y: Double)

operator fun Point.plus(that: Point) = Point(this.x + that.x, this.y + that.y)
operator fun Point.minus(that: Point) = Point(this.x - that.x, this.y - that.y)