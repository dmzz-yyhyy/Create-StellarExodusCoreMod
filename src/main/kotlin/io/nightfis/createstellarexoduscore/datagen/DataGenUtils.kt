package io.nightfis.createstellarexoduscore.datagen

import io.nightfis.createstellarexoduscore.StellarExodusCore
import net.minecraftforge.client.model.generators.ModelFile

fun existingModelFile(path: String): ModelFile.ExistingModelFile =
    ModelFile.ExistingModelFile(StellarExodusCore.of(path), ModDataGen.existingFileHelper)