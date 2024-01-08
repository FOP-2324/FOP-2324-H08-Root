package h08.transformer;

import org.objectweb.asm.*;
import org.sourcegrade.jagr.api.testing.ClassTransformer;

public class SystemNanoTimeTransformer implements ClassTransformer {
    @Override
    public String getName() {
        return "SystemNanoTimeTransformer";
    }

    @Override
    public void transform(ClassReader reader, ClassWriter writer) {
        if (reader.getClassName().equals("h08/Bank")) {
            reader.accept(new CV(Opcodes.ASM9, writer), 0);
        } else {
            reader.accept(writer, 0);
        }
    }

    private static class CV extends ClassVisitor {

        public CV(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return new MV(api, super.visitMethod(access, name, descriptor, signature, exceptions));
        }
    }

    private static class MV extends MethodVisitor {

        public MV(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (owner.equals("java/lang/System") && name.equals("nanoTime")) {
                visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "h08/transformer/SystemNanoTimeTransformer",
                    "nanoTime",
                    "()J",
                    false
                );
            } else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        }
    }

    public static long systemNanoTime = -1;

    @SuppressWarnings("unused") // will be called instead of System.nanoTime() inside of the Bank class
    public static long nanoTime() {
        if (systemNanoTime < 0) {
            return System.nanoTime();
        }

        return systemNanoTime;
    }
}
