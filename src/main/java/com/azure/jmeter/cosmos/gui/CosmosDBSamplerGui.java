/*
 * Copyright (c) Microsoft Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.azure.jmeter.cosmos.gui;

import com.azure.jmeter.cosmos.CosmosDBSampler;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

public class CosmosDBSamplerGui extends AbstractSamplerGui {

    private final JTextField cosmosDBURI = new JTextField();
    private final JTextField databaseName = new JTextField();
    private final JTextField cosmosDBKey = new JTextField();
    private final JTextField containerID = new JTextField();
    private final JTextField partitionKeyPath = new JTextField();
    private final JTextArea cosmosDBQuery = new JTextArea();
    private final JTextField runID = new JTextField();
    private final JTextField queryType = new JTextField();

    public CosmosDBSamplerGui() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createCosmosDBSamplerPanel(), BorderLayout.AFTER_LAST_LINE);
        add(createCosmosDBQueryPanel(), BorderLayout.CENTER);
    }

    private JPanel createCosmosDBQueryPanel() {
        JPanel cosmosDBQueryPanel = new JPanel(new BorderLayout(5, 0));
        cosmosDBQueryPanel.setBorder(BorderFactory.createTitledBorder("CosmosDB Query"));
        GroupLayout layout = new GroupLayout(cosmosDBQueryPanel);
        cosmosDBQueryPanel.setLayout(layout);

        JLabel queryLabel = new JLabel("Query:");

        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addComponent(queryLabel)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(cosmosDBQuery)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(queryLabel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cosmosDBQuery, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)));

        return cosmosDBQueryPanel;

    }

    private JPanel createCosmosDBSamplerPanel() {
        JPanel cosmosSamplerPanel = new JPanel();
        cosmosSamplerPanel.setBorder(BorderFactory.createTitledBorder("CosmosDB Configuration"));
        GroupLayout layout = new GroupLayout(cosmosSamplerPanel);
        cosmosSamplerPanel.setLayout(layout);

        JLabel cosmosDBURILabel = new JLabel("CosmosDB URI");
        JLabel cosmosDBKeyLabel = new JLabel("CosmosDB Key");
        JLabel databaseNameLabel = new JLabel("Database Name");
        JLabel containerIDLabel = new JLabel("Container ID");
        JLabel partitionKeyPathLabel = new JLabel("PartitionKey Path");
        JLabel runIDJLabel = new JLabel("RunID");
        JLabel queryTypeJLabel = new JLabel("Query Type");

        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addComponent(databaseNameLabel)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(databaseName))
                .addGroup(layout.createSequentialGroup().addComponent(cosmosDBURILabel)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(cosmosDBURI))
                .addGroup(layout.createSequentialGroup().addComponent(cosmosDBKeyLabel)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(cosmosDBKey))
                .addGroup(layout.createSequentialGroup().addComponent(containerIDLabel)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(containerID))
                .addGroup(layout.createSequentialGroup().addComponent(partitionKeyPathLabel)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(partitionKeyPath))
                .addGroup(layout.createSequentialGroup().addComponent(runIDJLabel)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(runID))
                .addGroup(layout.createSequentialGroup().addComponent(queryTypeJLabel)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(queryType)));

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(databaseNameLabel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(databaseName, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(cosmosDBURILabel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cosmosDBURI, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(cosmosDBKeyLabel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(cosmosDBKey, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(containerIDLabel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(containerID, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(partitionKeyPathLabel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(partitionKeyPath, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(runIDJLabel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(runID, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(queryTypeJLabel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(queryType, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE)));
        return cosmosSamplerPanel;
    }

    @Override
    public String getLabelResource() {
        return "Azure CosmosDB Sampler";
    }

    @Override
    public String getStaticLabel() {
        return getLabelResource();
    }

    @Override
    public TestElement createTestElement() {
        CosmosDBSampler cosmosDBSampler = new CosmosDBSampler();
        configureTestElement(cosmosDBSampler);
        return cosmosDBSampler;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        element.clear();
        super.configureTestElement(element);
        if (element instanceof CosmosDBSampler) {
            CosmosDBSampler cosmosDBSampler = (CosmosDBSampler) element;
            cosmosDBSampler.setCosmosDBURI(cosmosDBURI.getText());
            cosmosDBSampler.setCosmosDBKey(cosmosDBKey.getText());
            cosmosDBSampler.setDatabaseName(databaseName.getText());
            cosmosDBSampler.setContainerID(containerID.getText());
            cosmosDBSampler.setPartitionKeyPath(partitionKeyPath.getText());
            cosmosDBSampler.setCosmosDBQuery(cosmosDBQuery.getText());
            cosmosDBSampler.setRunID(runID.getText());
            cosmosDBSampler.setQueryType(queryType.getText());

        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof CosmosDBSampler) {
            CosmosDBSampler cosmosDBSampler = (CosmosDBSampler) element;
            cosmosDBURI.setText(cosmosDBSampler.getCosmosDBURI());
            cosmosDBKey.setText(cosmosDBSampler.getCosmosDBKey());
            databaseName.setText(cosmosDBSampler.getDatabaseName());
            containerID.setText(cosmosDBSampler.getContainerID());
            partitionKeyPath.setText(cosmosDBSampler.getPartitionKeyPath());
            cosmosDBQuery.setText(cosmosDBSampler.getCosmosDBQuery());
            runID.setText(cosmosDBSampler.getRunID());
            queryType.setText(cosmosDBSampler.getQueryType());
        }
    }
}
