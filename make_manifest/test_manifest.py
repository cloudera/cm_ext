#!/usr/bin/env python
#
# Licensed to Cloudera, Inc. under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  Cloudera, Inc. licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import json
import make_manifest
import test_deep_equality
import unittest

class TestMakeManifest(unittest.TestCase):
  def test_make_manifest(self):
    manifest_json = make_manifest.make_manifest('test_artifacts', 0)
    with open('test_artifacts/expected.json') as fp:
      expected_json = fp.read()
      manifest = json.loads(manifest_json)
      expected = json.loads(expected_json)
      test_deep_equality.deep_eq(manifest, expected, _assert=True)

if __name__ == "__main__":
  unittest.main()