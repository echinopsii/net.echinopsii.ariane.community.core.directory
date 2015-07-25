/**
 * Directory WAT
 * Team to JSON tools
 * Copyright (C) 06/05/14 echinopsii
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.echinopsii.ariane.community.core.directory.base.json.ds.organisational;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.directory.base.iPojo.DirectoryTreeMenuRootsRegistryImpl;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Team;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Our own object to JSON tools as :
 *  - we can have cycle in object graphs
 *  - we still want to have references to linked objects through IDs (and so we don't want @XmlTransient or @JsonIgnore)
 */
public class TeamJSON {

    public final static String TEAM_ID          = "teamID";
    public final static String TEAM_VERSION     = "teamVersion";
    public final static String TEAM_NAME        = "teamName";
    public final static String TEAM_DESCRIPTION = "teamDescription";
    public final static String TEAM_COLOR_CODE  = "teamColorCode";
    public final static String TEAM_OSI_ID      = "teamOSInstancesID";
    public final static String TEAM_APP_ID      = "teamApplicationsID";

    public final static void team2JSON(Team team, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(TEAM_ID, team.getId());
        jgenerator.writeNumberField(TEAM_VERSION, team.getVersion());
        jgenerator.writeStringField(TEAM_NAME, team.getName());
        jgenerator.writeStringField(TEAM_DESCRIPTION, team.getDescription());
        jgenerator.writeStringField(TEAM_COLOR_CODE, team.getColorCode());
        jgenerator.writeArrayFieldStart(TEAM_OSI_ID);
        for (OSInstance osi : team.getOsInstances())
            jgenerator.writeNumber(osi.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(TEAM_APP_ID);
        for (Application app : team.getApplications())
            jgenerator.writeNumber(app.getId());
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
    }

    public final static void oneTeam2JSON(Team team, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        TeamJSON.team2JSON(team, jgenerator);
        jgenerator.close();
    }

    public final static void manyTeams2JSON(HashSet<Team> teams, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("teams");
        Iterator<Team> iter = teams.iterator();
        while (iter.hasNext()) {
            Team current = iter.next();
            TeamJSON.team2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONFriendlyTeam {
        private long teamID;
        private long teamVersion;
        private String teamName;
        private String teamDescription;
        private String teamColorCode;
        private List<Long> teamOSInstancesID;
        private List<Long> teamApplicationsID;

        public List<Long> getTeamApplicationsID() {
            return teamApplicationsID;
        }

        public void setTeamApplicationsID(List<Long> teamApplicationsID) {
            this.teamApplicationsID = teamApplicationsID;
        }

        public List<Long> getTeamOSInstancesID() {
            return teamOSInstancesID;
        }

        public void setTeamOSInstancesID(List<Long> teamOSInstancesID) {
            this.teamOSInstancesID = teamOSInstancesID;
        }

        public String getTeamColorCode() {
            return teamColorCode;
        }

        public void setTeamColorCode(String teamColorCode) {
            this.teamColorCode = teamColorCode;
        }

        public String getTeamDescription() {
            return teamDescription;
        }

        public void setTeamDescription(String teamDescription) {
            this.teamDescription = teamDescription;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public long getTeamVersion() {
            return teamVersion;
        }

        public void setTeamVersion(long teamVersion) {
            this.teamVersion = teamVersion;
        }

        public long getTeamID() {
            return teamID;
        }

        public void setTeamID(long teamID) {
            this.teamID = teamID;
        }
    }

    public final static JSONFriendlyTeam JSON2Team(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONFriendlyTeam jsonFriendlyTeam = mapper.readValue(payload, JSONFriendlyTeam.class);
        return jsonFriendlyTeam;
    }
}