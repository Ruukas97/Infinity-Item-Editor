package ruukas.infinityeditor.gui.monsteregg;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.EntityHorse;

public class MobTag
{
    // ENTITY TAGS
    public static final MobTag fire = new MobTagSliderShort( "tag.fire", "Fire", 0f, 500f, 20f );
    public static final MobTag noGravity = new MobTagToggle( "tag.nogravity", "NoGravity" );
    public static final MobTag invulnerable = new MobTagToggle( "tag.invulnerable", "Invulnerable" );
    public static final MobTag customName = new MobTagString( "tag.customname", "CustomName" );
    public static final MobTag customNameVisible = new MobTagToggle( "tag.customnamevisible", "CustomNameVisible" );
    public static final MobTag silent = new MobTagToggle( "tag.silent", "Silent" );
    public static final MobTag glowing = new MobTagToggle( "tag.glowing", "Glowing" );
    
    // MOB TAGS
    // public static final MobTag health = new MobTagSlider("tag.health", "Health", 0f, 200f, 1f);
    public static final MobTag absorption = new MobTagSlider( "tag.absorption", "AbsorptionAmount", 0f, 200f, 1f );
    public static final MobTag fallFlying = new MobTagToggle( "tag.fallflying", "FallFlying" );
    public static final MobTag canPickUpLoot = new MobTagToggle( "tag.canpickuploot", "CanPickUpLoot" );
    public static final MobTag noAI = new MobTagToggle( "tag.noai", "NoAI" );
    public static final MobTag persistenceRequired = new MobTagToggle( "tag.persistencerequired", "PersistenceRequired" );
    // TODO Equipment
    
    // CHICKEN
    public static final MobTag eggLayTime = new MobTagSliderInt( "tag.chicken.egglaytime", "EggLayTime", 0f, 20000f, 100f, 6000f );
    
    // CREEPER TAGS
    public static final MobTag powered = new MobTagToggle( "tag.creeper.powered", "powered" );
    public static final MobTag explosionRadius = new MobTagSliderByte( "tag.creeper.explosionradius", "ExplosionRadius", 0, Byte.MAX_VALUE, 1, 3 );
    public static final MobTag fuse = new MobTagSliderShort( "tag.creeper.fuse", "Fuse", 0f, 500f, 10f, 30f );
    public static final MobTag ignited = new MobTagToggle( "tag.creeper.ignited", "ignited" );
    
    // ENDERMAN
    // TODO
    /*-
     * carried: ID of the block carried by the enderman. When not carrying anything, 0. When loading, may also be a string block name.
     * carriedData: Additional data about the block carried by the enderman. 0 when not carrying anything.
     */
    
    // GHAST
    // ExplosionPower (Int slider) default: 1
    
    // ENDERMITE
    public static final MobTag lifeTimeEndermite = new MobTagSliderInt( "tag.endermite.lifetime", "LifeTime", 0, 3000f, 10f );
    public static final MobTag playerSpawnedEndermite = new MobTagToggle( "tag.endermite.playerspawned", "PlayerSpawned" );
    
    // HORSE
    // TOGGLE SADDLE
    // HORSE ARMOR LIST
    // TAMED TO PLAYER OR OTHER PLAYERS (Tame: byte)
    // http://minecraft.gamepedia.com/Horse#Data_values
    /**
     * @see EntityHorse#setHorseTexturePaths()
     */
    // Variants and markings
    
    // PARROT
    public static final MobTag variantParrot = new MobTagList( "tag.parrot.variant", "Variant", 5, true );
    
    // PIG
    public static final MobTag pigSaddled = new MobTagToggle( "tag.pig.saddled", "Saddle" );
    
    // SHEEP
    public static final MobTag colorSheep = new MobTagList( "tag.sheep.color", "Color", 16 );
    public static final MobTag sheared = new MobTagToggle( "tag.sheep.sheared", "Sheared" );
    
    // SHULKER
    public static final MobTag colorShulker = new MobTagList( "tag.shulker.color", "Color", 16 );
    
    // SLIME OR MAGMA
    public static final MobTag sizeSlime = new MobTagSliderInt( "tag.slime.size", "Size", 0f, 50f, 1f, 3f );
    
    // ZOMBIE OR HUSK
    public static final MobTag isBabyZombie = new MobTagToggle( "tag.zombie.isbaby", "IsBaby" );
    public static final MobTag canBreakDoors = new MobTagToggle( "tag.zombie.canbreakdoors", "CanBreakDoors" );
    
    // ZOMBIE VILLAGER
    // professionVillager - already registered
    
    // ZOMBIE PIGMAN
    public static final MobTag angerZombiePigman = new MobTagSliderShort( "tag.zombiepigman.anger", "Anger", 0f, 1000f, 10f );
    
    // VILLAGER
    public static final MobTag professionVillager = new MobTagList( "tag.villager.profession", "Profession", 6, true );
    public static final MobTag willingVillager = new MobTagToggle( "tag.villager.willing", "Willing" );
    // public static final MobTag careerVillager = new MobTagList( "tag.villager.career", "Career", 0, true );
    
    // VEX
    // LifeTicks int slider (left to live) (probably no reason to add)
    
    // VINDICATOR
    public static final MobTag johnny = new MobTagToggle( "tag.vindicator.johnny", "Johnny" );
    
    public static MobTag[] ENTITY_SPECIFIC = new MobTag[] { fire, noGravity, invulnerable, silent, customNameVisible, customName, glowing };
    public static MobTag[] MOB_SPECIFIC = new MobTag[] { absorption, fallFlying, canPickUpLoot, noAI, persistenceRequired };
    
    public static MobTag[] CHICKEN_SPECIFIC = new MobTag[] { eggLayTime };
    public static MobTag[] CREEPER_SPECIFIC = new MobTag[] { powered, explosionRadius, fuse, ignited };
    public static MobTag[] ENDERMITE_SPECIFIC = new MobTag[] { lifeTimeEndermite, playerSpawnedEndermite };
    public static MobTag[] PARROT_SPECIFIC = new MobTag[] { variantParrot };
    public static MobTag[] PIG_SPECIFIC = new MobTag[] { pigSaddled };
    public static MobTag[] SHEEP_SPECIFIC = new MobTag[] { colorSheep, sheared };
    public static MobTag[] SHULKER_SPECIFIC = new MobTag[] { colorShulker };
    public static MobTag[] SLIME_SPECIFIC = new MobTag[] { sizeSlime };
    public static MobTag[] VILLAGER_SPECIFIC = new MobTag[] { professionVillager, willingVillager };
    public static MobTag[] VINDICATOR_SPECIFIC = new MobTag[] { johnny };
    public static MobTag[] ZOMBIE_SPECIFIC = new MobTag[] { isBabyZombie, canBreakDoors };
    public static MobTag[] ZOMBIEVILLAGER_SPECIFIC = new MobTag[] { isBabyZombie, canBreakDoors, professionVillager };
    public static MobTag[] ZOMBIEPIGMAN_SPECIFIC = new MobTag[] { isBabyZombie, canBreakDoors, angerZombiePigman };
    
    protected final String name, key;
    
    protected MobTag(String name, String key) {
        this.name = name;
        this.key = key;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getTranslatedName()
    {
        return I18n.format( name );
    }
}
