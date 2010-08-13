/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.repo.action.scheduled;

import java.util.List;

import org.alfresco.service.cmr.action.Action;

/**
 * A service which handles the scheduling of the
 *  execution of persisted actions.
 * It handles registering them with the Quartz
 *  scheduler on repository start, and handles
 *  the edit, creation and deletion of them.
 * 
 * @author Nick Burch
 * @since 3.4
 */
public interface ScheduledPersistedActionService {
   /**
    * Creates a new schedule, for the specified Action.
    */
   public ScheduledPersistedAction createSchedule(Action persistedAction);
   
   /**
    * Saves the changes to the schedule to the repository,
    *  and updates the Scheduler with any changed details.
    */
   public void saveSchedule(ScheduledPersistedAction schedule);
   
   /**
    * Removes the schedule for the action, and cancels future
    *  executions of it.
    * The persisted action is unchanged.
    */
   public void deleteSchedule(ScheduledPersistedAction schedule);
   
   /**
    * Returns the schedule for the specified action, or
    *  null if it isn't currently scheduled. 
    */
   public ScheduledPersistedAction getSchedule(Action persistedAction);
   
   /**
    * Returns all currently scheduled actions.
    */
   public List<ScheduledPersistedAction> listSchedules();
}
