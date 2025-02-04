package org.cyclops.everlastingabilities.api.capability;

import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Wrapper for a tag-based ability store.
 * @author rubensworks
 */
public class CompoundTagMutableAbilityStore implements IMutableAbilityStore {

    private static final String NBT_STORE = Reference.MOD_ID + ":abilityStoreStack";

    private final Supplier<CompoundTag> tagSupplier;
    private final RegistryAccess registryAccess;

    public CompoundTagMutableAbilityStore(Supplier<CompoundTag> tagSupplier, RegistryAccess registryAccess) {
        this.tagSupplier = tagSupplier;
        this.registryAccess = registryAccess;
    }

    protected Registry<IAbilityType> getRegistry() {
         return AbilityHelpers.getRegistry(this.registryAccess);
    }

    public boolean isInitialized() {
        CompoundTag root = tagSupplier.get();
        return root.contains(NBT_STORE);
    }

    protected IMutableAbilityStore getInnerStore() {
        IMutableAbilityStore store = new DefaultMutableAbilityStore();
        CompoundTag root = tagSupplier.get();
        if (!root.contains(NBT_STORE)) {
            root.put(NBT_STORE, new ListTag());
        }
        Tag nbt = root.get(NBT_STORE);
        AbilityHelpers.deserialize(getRegistry(), store, nbt);
        return store;
    }

    protected IMutableAbilityStore setInnerStore(IMutableAbilityStore store) {
        CompoundTag root = tagSupplier.get();
        Tag nbt = AbilityHelpers.serialize(getRegistry(), store);
        root.put(NBT_STORE, nbt);
        return store;
    }

    @NonNull
    @Override
    public Ability addAbility(Ability ability, boolean doAdd) {
        IMutableAbilityStore store = getInnerStore();
        Ability ret = store.addAbility(ability, doAdd);
        setInnerStore(store);
        return ret;
    }

    @NonNull
    @Override
    public Ability removeAbility(Ability ability, boolean doRemove) {
        IMutableAbilityStore store = getInnerStore();
        Ability ret = store.removeAbility(ability, doRemove);
        setInnerStore(store);
        return ret;
    }

    @Override
    public void setAbilities(Map<Holder<IAbilityType>, Integer> abilityTypes) {
        IMutableAbilityStore store = getInnerStore();
        store.setAbilities(abilityTypes);
        setInnerStore(store);
    }

    @Override
    public boolean hasAbilityType(Holder<IAbilityType> abilityType) {
        IMutableAbilityStore store = getInnerStore();
        return store.hasAbilityType(abilityType);
    }

    @Override
    public Collection<Holder<IAbilityType>> getAbilityTypes() {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbilityTypes();
    }

    @Override
    public Collection<Ability> getAbilities() {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbilities();
    }

    @Override
    public Map<Holder<IAbilityType>, Integer> getAbilitiesRaw() {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbilitiesRaw();
    }

    @Override
    public Ability getAbility(Holder<IAbilityType> abilityType) {
        IMutableAbilityStore store = getInnerStore();
        return store.getAbility(abilityType);
    }
}
