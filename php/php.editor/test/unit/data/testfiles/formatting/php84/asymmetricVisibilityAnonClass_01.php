<?php
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

$anon = new class() {
    // set visibility
public(set) string $publicSet; // implicit public
protected(set) string $protectedSet = "test"; // implicit public
private(set) string $privateSet; // implicit public
    // set visibility, readonly
public(set)               readonly string $publicSetReadonly; // implicit public
protected(set) readonly string $protectedSetReadonly; // implicit public
private(set)               readonly string $privateSetReadonly; // implicit public
     readonly            public(set) string $readonlyPublicSet; // implicit public
readonly protected(set) string $readonlyProtectedSet; // implicit public
readonly private(set) string $readonlyPrivateSet; // implicit public
    // visibility, set visibility
public               public(set) int $publicPublicSet;
public protected(set) int $publicProtectedSet;
public        private(set)               int $publicPrivateSet;
protected public(set) int $protectedPublicSet; // error
protected               protected(set) int $protectedProtectedSet = 1;
protected private(set) int $protectedPrivateSet;
              private public(set) int $privatePublicSet; // error
private protected(set) int $privateProtectedSet; // error
private private(set) int $privatePrivateSet;
public(set) public int $publicSetPublic;
              public(set) protected int $publicSetProtected; // error
public(set)               private int $publicSetPrivate; // error
protected(set) public int $protectedSetPublic;
protected(set)               protected int $protectedSetProtected;
protected(set) private int $protectedSetPrivate; // error
              private(set) public int $privateSetPublic;
private(set) protected int $privateSetProtected;
private(set) private int $privateSetPrivate = 1;
    // visibility, set visibility, readonly
public public(set) readonly $publicPublicSetReadonly; // error
public protected(set)               readonly int $publicProtectedSetReadonly;
public protected(set) readonly int $publicProtectedSetReadonly2 = 1; // error
public private(set)               readonly int $publicPrivateSetReadonly;
public readonly public(set) int $publicReadonlyPublicSet;
public readonly protected(set) int $publicReadonlyProtectedSet;
public readonly private(set) int $publicReadonlyPrivateSet;
protected public(set) readonly int $protectedPublicSetReadonly; // error
protected         protected(set) readonly int $protectedProtectedSetReadonly;
protected private(set)         readonly int $protectedPrivateSetReadonly;
protected         readonly public(set) int $protectedReadonlyPublicSet; // error
protected readonly         protected(set) int $protectedReadonlyProtectedSet;
protected readonly private(set) int $protectedReadonlyPrivateSet;
private         public(set) readonly int $privatePublicSetReadonly; // error
private protected(set) r        eadonly int $privateProtectedSetReadonly; // error
private private(set) readonly int $privatePrivateSetReadonly;
private readonly public(set)         int $privateReadonlyPublicSet; // error
private readonly protected(set) int $privateReadonlyProtectedSet; // error
private readonly private(set) int $privateReadonlyPrivateSet;
        public(set) public readonly string $publicSetPublicReadonly;
public(set) protected readonly string                 $publicSetProtectedReadonly; // error
public(set) private readonly string $publicSetPrivateReadonly; // error
public(set) readonly public string $publicSetReadonlyPublic;
        public(set) readonly protected                   string             $publicSetReadonlyProtected; // error
public(set)         readonly private string $publicSetReadonlyPrivate; // error
protected(set) public readonly string             $protectedSetPublicReadonly;
protected(set) protected         readonly string $protectedSetProtectedReadonly;
protected(set) private readonly string $protectedSetPrivateReadonly; // error
protected(set) readonly public string $protectedSetReadonlyPublic;
         protected(set)         readonly protected string $protectedSetReadonlyProtected;
protected(set) readonly private string $protectedSetReadonlyPrivate; // error
private(set) public readonly string $privateSetPublicReadonly;
                   private(set)           protected readonly string $privateSetProtectedReadonly;
private(set) private readonly string $privateSetPrivateReadonly;
private(set)   readonly public string $privateSetReadonlyPublic;
   private(set)           readonly protected string $privateSetReadonlyProtected;
private(set) readonly private string $privateSetReadonlyPrivate;
    readonly public public(set) string $readOnlyPublicPublicSet;
readonly public          protected(set)           string                   $readOnlyPublicProtectedSet;
readonly public            private(set) string $readOnlyPublicPrivateSet;
readonly protected public(set) string $readOnlyProtectedPublicSet; // error
readonly protected                 protected(set) string               $readOnlyProtectedProtectedSet;
readonly protected private(set) string $readOnlyProtectedPrivateSet;
readonly private                    public(set)                     string $readOnlyPrivatePublicSet; // error
readonly private           protected(set) string             $readOnlyPrivateProtectedSet; // error
readonly               private private(set) string                $readOnlyPrivatePrivateSet;
           readonly            public(set)             public string                    readOnlyPublicSetPublic;
readonly public(set) protected string $readOnlyPublicSetProtected; // error
         readonly           public(set) private string $readOnlyPublicSetPrivate; // error
readonly protected(set) public string $readOnlyProtectedSetPublic;
readonly           protected(set) protected    string  |  int $readOnlyProtectedSetProtected;
readonly protected(set) private string $readOnlyProtectedSetPrivate; // error
readonly             private(set) public string $readOnlyPrivateSetPublic;
readonly private(set)               protected string $readOnlyPrivateSetProtected;
             readonly            private(set) private            string              $readOnlyPrivateSetPrivate;
};
