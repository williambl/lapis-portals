package com.williambl.lapiswarps

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.DoorBlock
import net.minecraft.block.enums.DoubleBlockHalf
import net.minecraft.tag.BlockTags
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

fun init() {
    UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
        if (world.isClient)
            return@register ActionResult.PASS

        val blockState = world.getBlockState(hitResult.blockPos)
        if (blockState.isIn(BlockTags.DOORS) && blockState[DoorBlock.OPEN])
            println(checkForMultiBlock(world, getLowerDoorPos(hitResult.blockPos, blockState), blockState[DoorBlock.FACING].opposite))

        return@register ActionResult.PASS
    }
}

fun getLowerDoorPos(pos: BlockPos, blockState: BlockState): BlockPos =
        if (blockState[DoorBlock.HALF] == DoubleBlockHalf.LOWER)
            pos
        else
            pos.down()

/**
 * @param pos the position of the *lower* door block
 * @param dir the 'forwards' direction of the door
 */
fun checkForMultiBlock(world: World, pos: BlockPos, dir: Direction): Boolean {
    val mut = pos.mutableCopy()

    val left = dir.rotateLeft()
    val right = dir.rotateRight()

    return listOf(
            world.getBlockState(mut.move(Direction.DOWN)),
            world.getBlockState(mut.move(Direction.UP).move(left)),
            world.getBlockState(mut.move(Direction.UP)),
            world.getBlockState(mut.set(pos).move(right)),
            world.getBlockState(mut.move(Direction.UP)),
            world.getBlockState(mut.set(pos).move(Direction.UP, 2)),
            world.getBlockState(mut.set(pos).move(dir.opposite)),
            world.getBlockState(mut.move(Direction.UP))
    ).all { it.block == Blocks.LAPIS_BLOCK } //TODO make it a tag
}