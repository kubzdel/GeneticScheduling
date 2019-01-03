class CMDArgumentsParser {
    companion object {
        fun validateArguments(args: Array<String>) : InstanceProperties{
            assert(args.size > 2) {"There must be at least 3 elements"}
            println(args.joinToString(" "))
            val n = Integer.parseInt(args[0])
            val k = Integer.parseInt(args[1])
            val h = java.lang.Double.parseDouble(args[2])
            assert(n in arrayOf(10, 20, 50, 100, 200, 500, 1000)) {"n=$n is not in the range [10, 20, 50, 100, 200, 500, 1000]."}
            assert(k in 1..10) {"k=$k is not valid. It must be a natural number in range <0,9>"}
            assert(h in 0..1) {"h=$h is not valid. It must be a real number in range <0,1>"}

            return InstanceProperties(n, k, h)
        }
    }
}