package org.redlance.dima_dencep.mods.translationfallbacks.adventure.patches;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ComponentSerializerImpl {
    public static byte[] patch(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("read") && mn.desc.equals("(Lcom/google/gson/stream/JsonReader;)Lnet/kyori/adventure/text/BuildableComponent;")) {
                modifyRead(cn, mn);
            } else if (mn.name.equals("write") && mn.desc.equals("(Lcom/google/gson/stream/JsonWriter;Lnet/kyori/adventure/text/Component;)V")) {
                modifyWrite(cn, mn);
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private static void modifyRead(ClassNode cn, MethodNode mn) {
        int styleSlot = -1;
        for (LocalVariableNode lvn : mn.localVariables) {
            if (lvn.name.equals("style")) {
                styleSlot = lvn.index;
            }
        }
        if (styleSlot == -1) {
            // No debug info or variable not found, skip
            return;
        }

        InsnList insns = mn.instructions;
        AbstractInsnNode node = insns.getFirst();
        while (node != null) {
            if (node instanceof MethodInsnNode min && node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                if (min.owner.equals("com/google/gson/stream/JsonReader") && min.name.equals("endObject") && min.desc.equals("()V")) {
                    // Find the next invokeinterface build()
                    AbstractInsnNode next = node.getNext();
                    while (next != null) {
                        if (next instanceof MethodInsnNode buildCall && next.getOpcode() == Opcodes.INVOKEINTERFACE) {
                            if (buildCall.name.equals("build") && buildCall.desc.equals("()Lnet/kyori/adventure/text/BuildableComponent;") && buildCall.itf) {
                                // Insert after build call
                                InsnList insert = new InsnList();
                                insert.add(new VarInsnNode(Opcodes.ALOAD, styleSlot));
                                insert.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insert.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, "gson", "Lcom/google/gson/Gson;"));
                                insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/redlance/dima_dencep/mods/translationfallbacks/adventure/ComponentSerializerImplHooks", "doRead", "(Lnet/kyori/adventure/text/BuildableComponent;Lcom/google/gson/JsonObject;Lcom/google/gson/Gson;)Lnet/kyori/adventure/text/BuildableComponent;", false));
                                insns.insert(buildCall, insert);
                                return; // Assume only one such place
                            }
                        }
                        next = next.getNext();
                    }
                }
            }
            node = node.getNext();
        }
    }

    private static void modifyWrite(ClassNode cn, MethodNode mn) {
        int translatableSlot = -1;
        for (LocalVariableNode lvn : mn.localVariables) {
            if (lvn.name.equals("translatable")) {
                translatableSlot = lvn.index;
            }
        }
        if (translatableSlot == -1) {
            return;
        }

        InsnList insns = mn.instructions;
        AbstractInsnNode node = insns.getFirst();
        while (node != null) {
            if (node instanceof MethodInsnNode min && node.getOpcode() == Opcodes.INVOKEINTERFACE) {
                if (min.owner.equals("net/kyori/adventure/text/TranslatableComponent") && min.name.equals("arguments") && min.desc.equals("()Ljava/util/List;")) {
                    // Next should be invoke isEmpty
                    AbstractInsnNode next = node.getNext();
                    if (next instanceof MethodInsnNode isEmptyCall && next.getOpcode() == Opcodes.INVOKEINTERFACE) {
                        if (isEmptyCall.owner.equals("java/util/List") && isEmptyCall.name.equals("isEmpty") && isEmptyCall.desc.equals("()Z")) {
                            // Next should be IFNE
                            AbstractInsnNode jumpNode = next.getNext();
                            if (jumpNode.getOpcode() == Opcodes.IFNE) {
                                LabelNode skipLabel = ((JumpInsnNode) jumpNode).label;
                                // Insert after the skipLabel
                                InsnList insert = new InsnList();
                                insert.add(new VarInsnNode(Opcodes.ALOAD, translatableSlot));
                                insert.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                insert.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                insert.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, "gson", "Lcom/google/gson/Gson;"));
                                insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/redlance/dima_dencep/mods/translationfallbacks/adventure/ComponentSerializerImplHooks", "doWrite", "(Lnet/kyori/adventure/text/TranslatableComponent;Lcom/google/gson/stream/JsonWriter;Lcom/google/gson/Gson;)V", false));
                                insns.insert(skipLabel, insert);
                                return; // Assume only one such place
                            }
                        }
                    }
                }
            }
            node = node.getNext();
        }
    }
}
