package com.jjh.blesample.attrs;

/**
 * Created by jjh860627 on 2017. 10. 30..
 */

public class RACPAttributes {

    public static enum OpCode{
        REPORT_STORED_RECORDS((byte)1), DELETE_STORED_RECORDS((byte)2), ABORT_OPERATION((byte)3),
        REPORT_NUMBER_OF_STORED_RECORDS((byte)4), NUMBER_OF_STORED_RECORDS_RESPONSE((byte)5),
        RESPONSE_CODE((byte)6);

        public final byte code;
        private OpCode(byte code){
            this.code  = code;
        }

    }

    public static enum Operator{
        NULL((byte)0), ALL_RECORDS((byte)1), LESSTHAN_OR_EQUAL_TO((byte)2), GREATER_THAN_OR_EQUAL_TO((byte)3),
        WITHIN_RANGE_OF((byte)4), FIRST_RECORD((byte)5), LAST_RECORD((byte)6);

        public final byte code;
        private Operator(byte code){
            this.code  = code;
        }
    }

    public static enum ReponseCode{
        SUCCESS((byte)1), OPCODE_NOT_SUPPORTED((byte)2), INVALID_OPERATOR((byte)3),
        OPERATOR_NOT_SUPPORTED((byte)4), INVALID_OPERAND((byte)5), NO_RECORDS_FOUND((byte)6),
        ABORT_UNSUCCESSFUL((byte)7), PROCEDURE_NOT_COMPLETED((byte)8), OPERAND_NOT_SUPPORTED((byte)9);

        public final byte code;
        private ReponseCode(byte code){
            this.code  = code;
        }
    }
}
