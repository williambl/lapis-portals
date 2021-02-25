package com.williambl.lapiswarps

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimaps
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.DoorBlock
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.block.enums.DoubleBlockHalf
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.tag.BlockTags
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.random.asKotlinRandom

val portals = HashMultimap.create<Int, BlockPos>()

@ExperimentalContracts
fun init() {
    UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
        if (world.isClient)
            return@register ActionResult.PASS

        val blockState = world.getBlockState(hitResult.blockPos)
        if (blockState.isIn(BlockTags.DOORS) && blockState[DoorBlock.OPEN]) {
            val lowerDoorPos = getLowerDoorPos(hitResult.blockPos, blockState)
            val dir = blockState[DoorBlock.FACING].opposite
            if (!checkForMultiBlock(world, lowerDoorPos, dir)) return@register ActionResult.PASS

            val channel = getItems(world, lowerDoorPos, dir)

            val otherPortalPos = portals.get(channel)?.filterNot { it == lowerDoorPos }.run {
                if (this == null || this.isEmpty()) null else this.random(world.random.asKotlinRandom())
            }

            if (isValidPortal(world, otherPortalPos) && getItems(world, otherPortalPos, world.getBlockState(otherPortalPos)[DoorBlock.FACING].opposite) == channel) {
                world.setBlockState(otherPortalPos, world.getBlockState(otherPortalPos).with(DoorBlock.OPEN, true))
                player.teleport(otherPortalPos.x+0.5, otherPortalPos.y.toDouble(), otherPortalPos.z+0.5)
            } else if (otherPortalPos != null) {
                portals.remove(channel, otherPortalPos)
            }

            portals.asMap().asSequence().filter { it.key != channel && it.value.contains(lowerDoorPos) }.map { it.key }.forEach { portals.remove(it, lowerDoorPos) }

            if (!portals.containsValue(lowerDoorPos)) portals.put(channel, lowerDoorPos)
        }

        return@register ActionResult.PASS
    }
}

@ExperimentalContracts
fun isValidPortal(world: World, pos: BlockPos?): Boolean {
    contract {
        returns(true) implies (pos != null)
    }
    if (pos == null)
        return false
    val blockState = world.getBlockState(pos)
    return blockState.isIn(BlockTags.DOORS) && checkForMultiBlock(world, getLowerDoorPos(pos, blockState), blockState[DoorBlock.FACING].opposite)
}

fun getLowerDoorPos(pos: BlockPos, blockState: BlockState): BlockPos =
        if (blockState[DoorBlock.HALF] == DoubleBlockHalf.LOWER)
            pos
        else
            pos.down()

fun getItems(world: World, pos: BlockPos, dir: Direction): Int {
    val be = world.getBlockEntity(pos.mutableCopy().move(Direction.UP, 2).move(dir.opposite))
    val result = mutableListOf<Item>()

    if (be is Inventory) {
        for (i in 0 until be.size()) {
            result.add(be.getStack(i).item)
        }
    }

    return result.asSequence().distinct().map { Registry.ITEM.getId(it).toString() }.reduceOrNull(String::plus)?.hashCode() ?: 0
}

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