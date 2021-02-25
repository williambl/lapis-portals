package com.williambl.lapiswarps

import net.minecraft.util.math.Direction

// https://gist.github.com/Commoble/ec1c4574e74eb7220144815065e77a03

val D: Direction = Direction.DOWN
val U: Direction = Direction.UP
val N: Direction = Direction.NORTH
val S: Direction = Direction.SOUTH
val W: Direction = Direction.WEST
val E: Direction = Direction.EAST

val SAMES = arrayOf(D, U, N, S, W, E)
val OPPOSITES = arrayOf(U, D, S, N, E, W)
val ROTATE_X_DNUS = arrayOf(N, S, U, D, W, E)
val ROTATE_X_DSUN = arrayOf(S, N, D, U, W, E)
val ROTATE_Y_NESW = arrayOf(D, U, E, W, N, S)
val ROTATE_Y_NWSE = arrayOf(D, U, W, E, S, N)
val ROTATE_Z_DWUE = arrayOf(W, E, N, S, U, D)
val ROTATE_Z_DEUW = arrayOf(E, W, N, S, D, U)

val ORTHAGONAL_AXES = arrayOf(arrayOf(Direction.Axis.Y, Direction.Axis.X), arrayOf(Direction.Axis.X, Direction.Axis.Z), arrayOf(Direction.Axis.Y, Direction.Axis.Z))

val ORTHAGONAL_ROTATION_TABLE = arrayOf(arrayOf(N, E, S, W), arrayOf(S, E, N, W), arrayOf(U, E, D, W), arrayOf(D, E, U, W), arrayOf(D, S, U, N), arrayOf(U, S, D, N))

/** Indices are direction indices: `\[from]\[to]\[toRotate]`  */
val ROTATION_TABLE = arrayOf(arrayOf(
        SAMES,  // to = down
        OPPOSITES,  // to = up
        ROTATE_X_DNUS,  // down to north
        ROTATE_X_DSUN,  // down to south
        ROTATE_Z_DWUE,  // down to west
        ROTATE_Z_DEUW // down to east
), arrayOf(
        OPPOSITES,  // up to down
        SAMES,  // up to up
        ROTATE_X_DSUN,  // up to north
        ROTATE_X_DNUS,  // up to south
        ROTATE_Z_DEUW,  // up to west
        ROTATE_Z_DWUE), arrayOf(
        ROTATE_X_DSUN,  // north to down
        ROTATE_X_DNUS,  // north to up
        SAMES,
        OPPOSITES,
        ROTATE_Y_NWSE,  // north to west
        ROTATE_Y_NESW // north to east
), arrayOf(
        ROTATE_X_DNUS,  // south to down
        ROTATE_X_DSUN,  // south to up
        OPPOSITES,
        SAMES,
        ROTATE_Y_NESW,
        ROTATE_Y_NWSE
), arrayOf(
        ROTATE_Z_DEUW,  // west to down
        ROTATE_Z_DWUE,  // west to up
        ROTATE_Y_NESW,  // west to north
        ROTATE_Y_NWSE,  // west to south
        SAMES,
        OPPOSITES
), arrayOf(
        ROTATE_Z_DWUE,
        ROTATE_Z_DEUW,
        ROTATE_Y_NWSE,
        ROTATE_Y_NESW,
        OPPOSITES,
        SAMES
))

/**
 * Given two directions and a third, applies the rotation-of-the-first-direction-to-the-second
 * to the third direction
 * @param from
 * @param to
 * @param toRotate
 * @return the rotated direction
 */
fun getRotatedDirection(from: Direction, to: Direction, toRotate: Direction): Direction {
    return ROTATION_TABLE[from.id][to.id][toRotate.id]
}

fun Direction.rotateLeft(): Direction {
    return ROTATION_TABLE[N.id][W.id][this.id]
}

fun Direction.rotateRight(): Direction {
    return ROTATION_TABLE[N.id][E.id][this.id]
}