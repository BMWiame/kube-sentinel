package dev.wiame.sentinel_api.config;
import java.util.Set;

public final class SentinelConfig {
    private static volatile SentinelConfig instance;
    private volatile String scoringMode="highest";
    private final Set<String> allowedNamespaces=Set.of("demo");
    private SentinelConfig(){}
    public static SentinelConfig getInstance(){
        if(instance==null){
            synchronized (SentinelConfig.class) {
                if (instance == null){
                    instance =new SentinelConfig();
                }
            }
        }
        return instance;
    }
    public String getScoringMode(){
        return scoringMode;
    }
    public void setScoringMode(String scoringMode){
        this.scoringMode=scoringMode;
    }
    public boolean mayActOn(String namespace){
        return allowedNamespaces.contains(namespace);
    }
}
