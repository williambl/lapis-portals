package com.williambl.lapiswarps

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import dev.onyxstudios.cca.api.v3.component.ComponentV3
import jdk.nashorn.internal.ir.Block
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos


interface LapisWarpsComponent: ComponentV3 {
    val portals: Multimap<Int, BlockPos>
    companion object {
        val key: ComponentKey<LapisWarpsComponent> = ComponentRegistryV3.INSTANCE.getOrCreate(Identifier("lapiswarps:lapiswarps"), LapisWarpsComponent::class.java)
    }
}

class LapisWarpsComponentImpl: LapisWarpsComponent {
    override val portals: Multimap<Int, BlockPos> = HashMultimap.create()

    override fun readFromNbt(tag: CompoundTag) {
        val keys = tag.getIntArray("Keys")
        val map = tag.getCompound("Map")

        keys.forEach { key -> map.getLongArray(key.toString()).forEach { value -> portals.put(key, BlockPos.fromLong(value)) } }
    }

    override fun writeToNbt(tag: CompoundTag) {
        tag.putIntArray("Keys", portals.keySet().toIntArray())

        val mapTag = CompoundTag()
        portals.asMap().forEach { entry ->
            mapTag.putLongArray(entry.key.toString(), entry.value.map { it.asLong() }.toLongArray())
        }
        tag.put("Map", mapTag)
    }
}