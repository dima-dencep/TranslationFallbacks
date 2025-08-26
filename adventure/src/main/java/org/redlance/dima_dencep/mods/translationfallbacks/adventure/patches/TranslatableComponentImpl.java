package org.redlance.dima_dencep.mods.translationfallbacks.adventure.patches;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class TranslatableComponentImpl {
    private static final String INTERFACE = "org/redlance/dima_dencep/mods/translationfallbacks/adventure/duck/FallbacksHolder";

    public static byte[] patch(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        // Add interface
        cn.interfaces.add(INTERFACE);

        // Add field
        cn.fields.add(new FieldNode(Opcodes.ACC_PRIVATE, "tf$fallbacks", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", null));

        // Modify constructor to initialize field to null
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("<init>") && mn.desc.equals("(Ljava/util/List;Lnet/kyori/adventure/text/format/Style;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V")) {
                InsnList insns = mn.instructions;
                AbstractInsnNode node = insns.getFirst();
                while (node != null) {
                    if (node.getOpcode() == Opcodes.INVOKESPECIAL) {
                        MethodInsnNode min = (MethodInsnNode) node;
                        if (min.owner.equals("net/kyori/adventure/text/AbstractComponent") && min.name.equals("<init>")) {
                            // Insert after super call
                            InsnList insert = new InsnList();
                            insert.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            insert.add(new InsnNode(Opcodes.ACONST_NULL));
                            insert.add(new FieldInsnNode(Opcodes.PUTFIELD, cn.name, "tf$fallbacks", "Ljava/util/Map;"));
                            insns.insert(min, insert);
                            break;
                        }
                    }
                    node = node.getNext();
                }
            }
        }

        // Add tf$get method
        MethodNode getMn = new MethodNode(Opcodes.ACC_PUBLIC, "tf$get", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", null);
        getMn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        getMn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, "tf$fallbacks", "Ljava/util/Map;"));
        getMn.instructions.add(new InsnNode(Opcodes.ARETURN));
        getMn.maxLocals = 1;
        getMn.maxStack = 1;
        cn.methods.add(getMn);

        // Add tf$set method
        MethodNode setMn = new MethodNode(Opcodes.ACC_PUBLIC, "tf$set", "(Ljava/util/Map;)V", "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V", null);
        setMn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        setMn.instructions.add(new LdcInsnNode("fallbacks"));
        setMn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false));
        setMn.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/util/Map"));
        setMn.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
        setMn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        setMn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        setMn.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, cn.name, "tf$fallbacks", "Ljava/util/Map;"));
        setMn.instructions.add(new InsnNode(Opcodes.RETURN));
        setMn.maxLocals = 2;
        setMn.maxStack = 2;
        cn.methods.add(setMn);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
