package opca.memorydb;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlRootElement;

import opca.model.OpinionSummary;
import opca.model.StatuteCitation;

import javax.xml.bind.annotation.XmlElement;

@XmlRootElement
public class MemoryDB {
    private TreeSet<OpinionSummary> opinionTable = new TreeSet<OpinionSummary>();
    private TreeSet<StatuteCitation> statuteTable = new TreeSet<StatuteCitation>();
    
    public TreeSet<OpinionSummary> getOpinionTable() {
        return opinionTable;
    }
    @XmlElement
    public void setOpinionTable(TreeSet<OpinionSummary> opinionTable) {
        this.opinionTable = opinionTable;
    }
    public TreeSet<StatuteCitation> getStatuteTable() {
        return statuteTable;
    }
    @XmlElement
    public void setStatuteTable(TreeSet<StatuteCitation> statuteTable) {
        this.statuteTable = statuteTable;
    }

}
