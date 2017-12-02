package com.isaac.phrases;

import com.isaac.utils.Common;
import edu.knowitall.openie.Argument;
import edu.knowitall.openie.Instance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ArgumentPhrase {
    /** Extracted Arguments (Arg-1, Rel, Arg-2s) and the confidence of such triple */
    private Double confidence;
    private String argument1;
    private String relation;
    private List<Argument2> argument2s;

    public ArgumentPhrase (Instance instance) {
        this.confidence = instance.confidence();
        this.argument1 = instance.extr().arg1().text();
        this.relation = instance.extr().rel().text();
        List<Argument> argumentList = Common.seq2List(instance.extr().arg2s());
        this.argument2s = new ArrayList<>();
        for (Argument argument : argumentList) argument2s.add(new Argument2(argument.text(), argument.getClass().getSimpleName()));
    }

    @Override public String toString () {
        List<String> argument2sString = argument2s.stream().map(Argument2::toString).collect(Collectors.toList());
        return String.join(" | ", argument1, relation, String.join(", ", argument2sString));
    }

    public String toStringWithConfidence () {
        return String.join(" | ", String.valueOf(confidence), argument1, relation,
                String.join(", ", argument2s.stream().map(Argument2::toString).collect(Collectors.toList())));
    }

    /** Getters */
    public Double getConfidence () { return confidence; }
    public String getArgument1 () { return argument1; }
    public String getRelation () { return relation; }
    public List<Argument2> getArgument2s () { return argument2s; }

    @SuppressWarnings("all")
    public class Argument2 {
        private String argument2;
        private String argumentType;

        public Argument2 (String argument2, String argumentType) {
            this.argument2 = argument2;
            this.argumentType = argumentType;
        }

        @Override public String toString () { return argument2.concat(" (").concat(argumentType).concat(")"); }

        /** Getters */
        public String getArgument2 () { return argument2; }
        public String getArgumentType () { return argumentType; }

        /** Setters */
        public void setArgument2 (String argument2) { this.argument2 = argument2; }
        public void setArgumentType (String argumentType) { this.argumentType = argumentType; }
    }
}
