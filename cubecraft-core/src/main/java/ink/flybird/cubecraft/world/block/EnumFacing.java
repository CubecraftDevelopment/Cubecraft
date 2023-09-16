package ink.flybird.cubecraft.world.block;


import ink.flybird.fcommon.container.Vector3;
import ink.flybird.fcommon.math.RotationMatrixConstants;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public enum EnumFacing {
    Up(0),
    Down(1),
    West(2),
    East(3),
    North(4),
    South(5);

    final byte numID;

    EnumFacing(int id) {
        this.numID = (byte) id;
    }

    public byte getNumID() {
        return numID;
    }

    public static EnumFacing fromId(int id) {
        return switch (id) {
            case 0 -> Up;
            case 1 -> Down;
            case 2 -> West;
            case 3 -> East;
            case 4 -> North;
            case 5 -> South;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    public Vector3<Long> findNear(long x, long y, long z, int radius) {
        switch (this.numID) {
            case 0 -> y += radius;
            case 1 -> y -= radius;
            case 2 -> z += radius;
            case 3 -> z -= radius;
            case 4 -> x += radius;
            case 5 -> x -= radius;
        }
        return new Vector3<>(x, y, z);
    }

    public static Vector3<Long> findNear(long x, long y, long z, int radius, int id) {
        switch (id) {
            case 0 -> y += radius;
            case 1 -> y -= radius;
            case 2 -> z += radius;
            case 3 -> z -= radius;
            case 4 -> x += radius;
            case 5 -> x -= radius;
        }
        return new Vector3<>(x, y, z);
    }

    public static EnumFacing[] all() {
        return new EnumFacing[]{
                Up,
                Down,
                West,
                East,
                North,
                South
        };
    }

    public static Matrix4d getMatrix(EnumFacing facing) {
        return switch (facing) {
            case Up -> RotationMatrixConstants.FACE_TOP;
            case Down -> RotationMatrixConstants.FACE_BOTTOM;
            case East -> RotationMatrixConstants.FACE_RIGHT;
            case West -> RotationMatrixConstants.FACE_LEFT;
            case North -> RotationMatrixConstants.FACE_BACK;
            case South -> RotationMatrixConstants.FACE_FRONT;
        };
    }


    /**
     * get axis aligned(world space facing)
     *
     * @param facing block face
     * @param face   face to query(relative)
     * @return axis aligned block facing
     */
    public static EnumFacing clip(EnumFacing facing, EnumFacing face) {
        return fromId(faceMapping[facing.getNumID()][face.getNumID()]);
    }

    /**
     * an alternative method of clip(BlockFacing,BlockFacing)
     *
     * @param facing block face
     * @param face   face to query(relative)
     * @return axis aligned block facing
     */
    public static int clip(int facing, int face) {
        return faceMapping[facing][face];
    }

    private static final int[][] faceMapping = new int[6][6];

    static {
        //todo:fix face mapping(culling and texture picking)
        faceMapping[0] = new int[]{0, 1, 2, 3, 4, 5};//up(default)
        faceMapping[1] = new int[]{5, 0, 1, 2, 3, 4};//down
        faceMapping[2] = new int[]{4, 5, 0, 1, 2, 3};//front
        faceMapping[3] = new int[]{3, 4, 5, 0, 1, 2};//back
        faceMapping[4] = new int[]{2, 3, 4, 5, 1, 1};//left
        faceMapping[5] = new int[]{1, 2, 3, 4, 5, 0};//right
    }

    public Vector3d clipVec(Vector3d vec) {
        return switch (this) {
            case Up -> new Vector3d(vec);
            case Down -> new Vector3d(vec).add(-0.5,-0.5,-0.5).mulProject(RotationMatrixConstants.FACE_BOTTOM).add(0.5,0.5,0.5);
            case South -> new Vector3d(vec).add(-0.5,-0.5,-0.5).mulProject(RotationMatrixConstants.FACE_BACK).add(0.5,0.5,0.5);
            case North -> new Vector3d(vec).add(-0.5,-0.5,-0.5).mulProject(RotationMatrixConstants.FACE_FRONT).add(0.5,0.5,0.5);
            case West -> new Vector3d(vec).add(-0.5,-0.5,-0.5).mulProject(RotationMatrixConstants.FACE_RIGHT).add(0.5,0.5,0.5);
            case East -> new Vector3d(vec).add(-0.5,-0.5,-0.5).mulProject(RotationMatrixConstants.FACE_LEFT).add(0.5,0.5,0.5);
        };

        /*
        return switch (this) {
            case Up -> new Vector3d(vec);
            case Down -> new Vector3d(vec.x, MathHelper.reflect(vec.y,0.5),vec.z);
            case South -> new Vector3d(MathHelper.reflect(vec.y,0.5),vec.x,vec.z);
            case North -> new Vector3d(vec.y,MathHelper.reflect(vec.x,0.5),vec.z);
            case West -> new Vector3d(vec.x,MathHelper.reflect(vec.z,0.5),vec.y);
            case East -> new Vector3d(vec.x,MathHelper.reflect(vec.z,0.5),MathHelper.reflect(vec.y,0.5));
        };

         */
    }
}
