package com.pgc.processor;

import com.pgc.model.Hero;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.entities.OnEntityCreated;
import skadistats.clarity.processor.entities.OnEntityDeleted;
import skadistats.clarity.processor.entities.OnEntityUpdated;

import java.util.HashMap;
import java.util.Map;

public class HeroProcessor {

    private final Map<Integer, Hero> heroes = new HashMap<>();

    @OnEntityCreated
    public void onEntityCreated(Entity entity) {

        String dtName = entity.getDtClass().getDtName();

        if (dtName.startsWith("CDOTA_Unit_Hero")) {

            int id = entity.getIndex();

            Hero hero = new Hero(id);

            hero.setName(dtName);

            heroes.put(id, hero);

            System.out.println("Hero creado: " + dtName);
        }
    }

    @OnEntityUpdated
    public void onEntityUpdated(Entity entity, FieldPath[] updatedPaths, int numUpdatedPaths) {

        String dtName = entity.getDtClass().getDtName();

        if (!dtName.startsWith("CDOTA_Unit_Hero")) {
            return;
        }

        Hero hero = heroes.get(entity.getIndex());

        if (hero == null) return;

        Integer health = entity.getProperty("m_iHealth");
        Integer x = entity.getProperty("CBodyComponent.m_cellX");
        Integer y = entity.getProperty("CBodyComponent.m_cellY");

        if (health != null) {
            hero.setHealth(health);
        }

        if (x != null && y != null) {
            hero.setPosition(x, y);
        }

        System.out.println(
                "Hero actualizado: " +
                        dtName +
                        " HP=" + health + " Pos_X=" + x + " Pos_Y=" + y
        );
    }

    @OnEntityDeleted
    public void onEntityDeleted(Entity entity) {

        heroes.remove(entity.getIndex());

        System.out.println(
                "Hero eliminado: " +
                        entity.getDtClass().getDtName()
        );
    }
}
